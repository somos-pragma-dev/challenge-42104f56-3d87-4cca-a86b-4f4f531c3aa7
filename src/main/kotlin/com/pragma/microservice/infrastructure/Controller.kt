package com.pragma.microservice.infrastructure

import com.pragma.microservice.application.RequestProcessor
import com.pragma.microservice.domain.ClientType
import com.pragma.microservice.domain.Completed
import com.pragma.microservice.domain.Invalid
import com.pragma.microservice.domain.Pending
import com.pragma.microservice.domain.Processing
import com.pragma.microservice.domain.Request
import com.pragma.microservice.domain.RequestStatus
import com.pragma.microservice.domain.ValidationResult
import com.pragma.microservice.infrastructure.client.ExternalInfoClient
import com.pragma.microservice.infrastructure.exception.ErrorResponse
import com.pragma.microservice.infrastructure.exception.RateLimitExceededException
import com.pragma.microservice.infrastructure.audit.AuditService
import io.ktor.http.HttpStatusCode
import io.ktor.http.HttpHeaders
import io.ktor.http.ContentType
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.plugins.requestvalidation.RequestValidationException
import io.ktor.server.plugins.requestvalidation.body
import io.ktor.server.plugins.requestvalidation.validate
import io.ktor.server.request.header
import io.ktor.server.request.path
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.delete
import io.ktor.server.routing.route
import io.ktor.utils.io.jvm.javaio.toInputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

private val logger: Logger = LoggerFactory.getLogger(Controller::class.java)

@Serializable
data class ProcessRequestInput(
    val clientId: String,
    val clientType: String,
    val operationKey: String,
    val requestData: String,
    val priority: Int = 0
)

@Serializable
data class ProcessResponse(
    val requestId: String,
    val status: String,
    val message: String,
    val processingTimeMs: Long? = null
)

@Serializable
data class HealthResponse(
    val status: String,
    val timestamp: Long,
    val uptimeSeconds: Long,
    val requestsProcessed: Int,
    val activeRequests: Int
)

@Serializable
data class MetricsResponse(
    val totalRequests: Int,
    val successfulRequests: Int,
    val failedRequests: Int,
    val averageProcessingTimeMs: Double,
    val requestsPerSecond: Double
)

class MicrosservicioController : KoinComponent {
    private val requestProcessor: RequestProcessor by inject()
    private val externalInfoClient: ExternalInfoClient by inject()
    private val auditService: AuditService by inject()
    
    private val requestCount = AtomicInteger(0)
    private val successCount = AtomicInteger(0)
    private val failureCount = AtomicInteger(0)
    private val processingTimes = ConcurrentHashMap<String, Long>()
    private val activeRequests = ConcurrentHashMap<String, Long>()
    private var applicationStartTime = System.currentTimeMillis()
    private val rateLimitMap = ConcurrentHashMap<String, Long>()
    
    private val rateLimitWindowMs = 60_000L
    private val maxRequestsPerWindow = 1000
    
    suspend fun processRequest(call: ApplicationCall) {
        val startTime = System.currentTimeMillis()
        val requestId = UUID.randomUUID().toString()
        
        try {
            val clientId = call.request.header("X-Client-Id") 
                ?: throw RequestValidationException(listOf("X-Client-Id header is required"))
            
            val idempotencyKey = call.request.header("X-Idempotency-Key")
            
            if (!checkRateLimit(clientId)) {
                throw RateLimitExceededException(
                    "Rate limit exceeded for client: $clientId",
                    retryAfterSeconds = 60
                )
            }
            
            activeRequests[requestId] = System.currentTimeMillis()
            requestCount.incrementAndGet()
            
            val input = call.receive<ProcessRequestInput>()
            
            logger.info("Processing request $requestId for client $clientId with operation key: ${input.operationKey}")
            
            val request = Request(
                id = requestId,
                clientId = input.clientId,
                clientType = ClientType.valueOf(input.clientType),
                operationKey = input.operationKey,
                requestData = input.requestData,
                priority = input.priority,
                status = Pending
            )
            
            val validation = request.validate()
            if (validation is Invalid) {
                logger.warn("Request validation failed: ${validation.reason}")
                call.respond(
                    HttpStatusCode.BadRequest,
                    ProcessResponse(
                        requestId = requestId,
                        status = "REJECTED",
                        message = validation.reason
                    )
                )
                return
            }
            
            val result = requestProcessor.processRequest(request, idempotencyKey)
            
            val processingTime = System.currentTimeMillis() - startTime
            processingTimes[requestId] = processingTime
            activeRequests.remove(requestId)
            
            if (result.status == Completed) {
                successCount.incrementAndGet()
            } else {
                failureCount.incrementAndGet()
            }
            
            logger.info("Request $requestId processed in ${processingTime}ms with status: ${result.status}")
            
            auditService.emitEvent(
                requestId = requestId,
                eventType = "REQUEST_PROCESSED",
                clientId = clientId,
                details = mapOf(
                    "status" to result.status.toString(),
                    "processingTimeMs" to processingTime
                )
            )
            
            call.respond(
                HttpStatusCode.OK,
                ProcessResponse(
                    requestId = requestId,
                    status = result.status.toString(),
                    message = "Request processed successfully",
                    processingTimeMs = processingTime
                )
            )
        } catch (e: Exception) {
            activeRequests.remove(requestId)
            failureCount.incrementAndGet()
            logger.error("Error processing request $requestId: ${e.message}", e)
            throw e
        }
    }
    
    suspend fun getRequestStatus(call: ApplicationCall) {
        val requestId = call.parameters["requestId"] 
            ?: throw RequestValidationException(listOf("requestId parameter is required"))
        
        logger.debug("Getting status for request: $requestId")
        
        val status = requestProcessor.getRequestStatus(requestId)
        
        if (status != null) {
            call.respond(HttpStatusCode.OK, status)
        } else {
            call.respond(
                HttpStatusCode.NotFound,
                ErrorResponse(
                    status = HttpStatusCode.NotFound.value,
                    error = "REQUEST_NOT_FOUND",
                    message = "Request with id $requestId not found",
                    path = call.request.path()
                )
            )
        }
    }
    
    suspend fun getHealth(call: ApplicationCall) {
        val uptimeSeconds = (System.currentTimeMillis() - applicationStartTime) / 1000
        
        call.respond(
            HttpStatusCode.OK,
            HealthResponse(
                status = "UP",
                timestamp = System.currentTimeMillis(),
                uptimeSeconds = uptimeSeconds,
                requestsProcessed = requestCount.get(),
                activeRequests = activeRequests.size
            )
        )
    }
    
    suspend fun getMetrics(call: ApplicationCall) {
        val avgTime = if (processingTimes.isNotEmpty()) {
            processingTimes.values.average()
        } else 0.0
        
        val uptimeSeconds = (System.currentTimeMillis() - applicationStartTime) / 1000
        val rps = if (uptimeSeconds > 0) {
            requestCount.get().toDouble() / uptimeSeconds
        } else 0.0
        
        call.respond(
            HttpStatusCode.OK,
            MetricsResponse(
                totalRequests = requestCount.get(),
                successfulRequests = successCount.get(),
                failedRequests = failureCount.get(),
                averageProcessingTimeMs = avgTime,
                requestsPerSecond = rps
            )
        )
    }
    
    suspend fun getExternalInfo(call: ApplicationCall) {
        val infoId = call.parameters["infoId"]
            ?: throw RequestValidationException(listOf("infoId parameter is required"))
        
        logger.debug("Fetching external info for id: $infoId")
        
        val info = withContext(Dispatchers.IO) {
            externalInfoClient.getClientInfo("DEFAULT", infoId)
        }
        
        if (info != null) {
            call.respond(HttpStatusCode.OK, info)
        } else {
            call.respond(
                HttpStatusCode.NotFound,
                ErrorResponse(
                    status = HttpStatusCode.NotFound.value,
                    error = "INFO_NOT_FOUND",
                    message = "External info with id $infoId not found",
                    path = call.request.path()
                )
            )
        }
    }
    
    suspend fun processBatch(call: ApplicationCall) {
        val requests = call.receive<List<ProcessRequestInput>>()
        
        logger.info("Processing batch of ${requests.size} requests")
        
        val results = withContext(Dispatchers.IO) {
            requests.map { input ->
                async {
                    try {
                        val requestId = UUID.randomUUID().toString()
                        val request = Request(
                            id = requestId,
                            clientId = input.clientId,
                            clientType = ClientType.valueOf(input.clientType),
                            operationKey = input.operationKey,
                            requestData = input.requestData,
                            priority = input.priority,
                            status = Pending
                        )
                        
                        requestProcessor.processRequest(request, null)
                    } catch (e: Exception) {
                        logger.error("Error processing batch request: ${e.message}", e)
                        null
                    }
                }
            }.awaitAll()
        }
        
        val successCount = results.count { it != null }
        val failCount = results.size - successCount
        
        logger.info("Batch processing complete: $successCount succeeded, $failCount failed")
        
        call.respond(
            HttpStatusCode.OK,
            mapOf(
                "total" to results.size,
                "succeeded" to successCount,
                "failed" to failCount
            )
        )
    }
    
    private fun checkRateLimit(clientId: String): Boolean {
        val now = System.currentTimeMillis()
        val windowStart = now - rateLimitWindowMs
        
        val clientRequests = rateLimitMap.entries
            .filter { it.value > windowStart }
            .toMutableList()
        
        val recentCount = clientRequests.size
        
        if (recentCount >= maxRequestsPerWindow) {
            logger.warn("Rate limit exceeded for client: $clientId")
            return false
        }
        
        rateLimitMap[clientId] = now
        
        clientRequests.forEach { (key, timestamp) ->
            if (timestamp < windowStart) {
                rateLimitMap.remove(key)
            }
        }
        
        return true
    }
}

fun Route.microservicioRoutes() {
    val controller = MicrosservicioController()
    
    route("/api/v1/requests") {
        post {
            controller.processRequest(call)
        }
        
        get("/{requestId}") {
            controller.getRequestStatus(call)
        }
        
        post("/batch") {
            controller.processBatch(call)
        }
    }
    
    route("/api/v1/health") {
        get {
            controller.getHealth(call)
        }
    }
    
    route("/api/v1/metrics") {
        get {
            controller.getMetrics(call)
        }
    }
    
    route("/api/v1/external") {
        get("/{infoId}") {
            controller.getExternalInfo(call)
        }
    }
}