package com.pragma.microservice.infrastructure.client

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.github.resilience4j.retry.annotation.Retry
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.math.pow

class ExternalInfoClient : KoinComponent {

    private val httpClient: HttpClient by inject()

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(ExternalInfoClient::class.java)
        private const val BASE_URL = "http://external-info-service:8080/api/v1"
        private const val DEFAULT_TIMEOUT_MS = 5000L
        private const val MAX_RETRY_ATTEMPTS = 3
        private const val EXPONENTIAL_BACKOFF_BASE = 2
    }

    @Retry(name = "externalInfoRetry")
    @CircuitBreaker(name = "externalInfoCircuitBreaker", fallbackMethod = "fallbackGetClientInfo")
    suspend fun getClientInfo(clientType: String, clientId: String): Map<String, Any>? {
        return withContext(Dispatchers.IO) {
            try {
                logger.debug("Fetching client info for type: $clientType, id: $clientId")
                
                val response: HttpResponse = httpClient.get("$BASE_URL/clients/info") {
                    contentType(ContentType.Application.Json)
                    parameter("type", clientType)
                    parameter("clientId", clientId)
                    timeout {
                        requestTimeoutMillis = DEFAULT_TIMEOUT_MS
                        connectTimeoutMillis = DEFAULT_TIMEOUT_MS
                        socketTimeoutMillis = DEFAULT_TIMEOUT_MS
                    }
                }
                
                if (response.status.value in 200..299) {
                    val body = response.body<String>()
                    logger.info("Successfully retrieved info for client: $clientId")
                    parseResponse(body)
                } else {
                    logger.warn("External service returned status: ${response.status.value}")
                    null
                }
            } catch (e: Exception) {
                logger.error("Error fetching client info: ${e.message}", e)
                throw e
            }
        }
    }

    @Retry(name = "externalInfoRetry")
    @CircuitBreaker(name = "externalInfoCircuitBreaker", fallbackMethod = "fallbackValidateClient")
    suspend fun validateClient(clientType: String, clientId: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                logger.debug("Validating client: type=$clientType, id=$clientId")
                
                val response: HttpResponse = httpClient.get("$BASE_URL/clients/validate") {
                    contentType(ContentType.Application.Json)
                    parameter("type", clientType)
                    parameter("clientId", clientId)
                    timeout {
                        requestTimeoutMillis = DEFAULT_TIMEOUT_MS
                        connectTimeoutMillis = DEFAULT_TIMEOUT_MS
                        socketTimeoutMillis = DEFAULT_TIMEOUT_MS
                    }
                }
                
                val isValid = response.status.value == 200
                logger.info("Client validation result for $clientId: $isValid")
                isValid
            } catch (e: Exception) {
                logger.error("Error validating client: ${e.message}", e)
                throw e
            }
        }
    }

    @Retry(name = "externalInfoRetry")
    @CircuitBreaker(name = "externalInfoCircuitBreaker", fallbackMethod = "fallbackGetClientHistory")
    suspend fun getClientHistory(clientId: String, limit: Int = 10): List<Map<String, Any>> {
        return withContext(Dispatchers.IO) {
            try {
                logger.debug("Fetching client history for id: $clientId, limit: $limit")
                
                val response: HttpResponse = httpClient.get("$BASE_URL/clients/history") {
                    contentType(ContentType.Application.Json)
                    parameter("clientId", clientId)
                    parameter("limit", limit)
                    timeout {
                        requestTimeoutMillis = DEFAULT_TIMEOUT_MS * 2
                        connectTimeoutMillis = DEFAULT_TIMEOUT_MS
                        socketTimeoutMillis = DEFAULT_TIMEOUT_MS * 2
                    }
                }
                
                if (response.status.value in 200..299) {
                    val body = response.body<String>()
                    logger.info("Successfully retrieved history for client: $clientId")
                    parseListResponse(body)
                } else {
                    logger.warn("External service returned status: ${response.status.value}")
                    emptyList()
                }
            } catch (e: Exception) {
                logger.error("Error fetching client history: ${e.message}", e)
                throw e
            }
        }
    }

    private fun parseResponse(body: String): Map<String, Any>? {
        return try {
            val jsonElement = kotlinx.serialization.json.Json.parseToJsonElement(body)
            if (jsonElement is kotlinx.serialization.json.JsonObject) {
                jsonElement.mapValues { (_, value) ->
                    when (value) {
                        is kotlinx.serialization.json.JsonPrimitive -> {
                            if (value.isString) value.content
                            else if (value.boolean != null) value.boolean
                            else value.content.toLongOrNull() ?: value.content.toDoubleOrNull() ?: value.content
                        }
                        is kotlinx.serialization.json.JsonObject -> parseResponse(value.toString())
                        is kotlinx.serialization.json.JsonArray -> parseListResponse(value.toString())
                        else -> value.toString()
                    }
                }
            } else null
        } catch (e: Exception) {
            logger.error("Error parsing response: ${e.message}", e)
            null
        }
    }

    private fun parseListResponse(body: String): List<Map<String, Any>> {
        return try {
            val jsonElement = kotlinx.serialization.json.Json.parseToJsonElement(body)
            if (jsonElement is kotlinx.serialization.json.JsonArray) {
                jsonElement.mapNotNull { element ->
                    if (element is kotlinx.serialization.json.JsonObject) {
                        parseResponse(element.toString())
                    } else null
                }
            } else emptyList()
        } catch (e: Exception) {
            logger.error("Error parsing list response: ${e.message}", e)
            emptyList()
        }
    }

    @Suppress("UNUSED")
    private fun fallbackGetClientInfo(clientType: String, clientId: String, e: Exception): Map<String, Any>? {
        logger.warn("Circuit breaker fallback triggered for getClientInfo: ${e.message}")
        return buildFallbackResponse(clientId)
    }

    @Suppress("UNUSED")
    private fun fallbackValidateClient(clientType: String, clientId: String, e: Exception): Boolean {
        logger.warn("Circuit breaker fallback triggered for validateClient: ${e.message}")
        return false
    }

    @Suppress("UNUSED")
    private fun fallbackGetClientHistory(clientId: String, limit: Int, e: Exception): List<Map<String, Any>> {
        logger.warn("Circuit breaker fallback triggered for getClientHistory: ${e.message}")
        return emptyList()
    }

    private fun buildFallbackResponse(clientId: String): Map<String, Any> {
        return mapOf(
            "clientId" to clientId,
            "status" to "UNKNOWN",
            "fallback" to true,
            "timestamp" to System.currentTimeMillis()
        )
    }

    suspend fun healthCheck(): Boolean {
        return try {
            val response: HttpResponse = httpClient.get("$BASE_URL/health")
            response.status.value == 200
        } catch (e: Exception) {
            logger.error("Health check failed: ${e.message}", e)
            false
        }
    }
}