package com.pragma.microservice.application

import com.pragma.microservice.domain.ClientType
import com.pragma.microservice.domain.ClientInfo
import com.pragma.microservice.domain.IdempotencyResult
import com.pragma.microservice.domain.Invalid
import com.pragma.microservice.domain.OperationKey
import com.pragma.microservice.domain.OperationKeyStatus
import com.pragma.microservice.domain.Request
import com.pragma.microservice.domain.RequestSummary
import com.pragma.microservice.domain.RequestStatus
import com.pragma.microservice.domain.Valid
import com.pragma.microservice.domain.ValidationResult
import com.pragma.microservice.infrastructure.client.ExternalInfoClient
import com.pragma.microservice.infrastructure.idempotency.IdempotencyRepository
import com.pragma.microservice.infrastructure.audit.AuditEvent
import com.pragma.microservice.infrastructure.audit.AuditService
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.retry.Retry
import io.github.resilience4j.bulkhead.ThreadPoolBulkhead
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Instant

class RequestProcessor : KoinComponent {
    private val externalInfoClient: ExternalInfoClient by inject()
    private val idempotencyRepository: IdempotencyRepository by inject()
    private val auditService: AuditService by inject()
    
    private val logger: Logger = LoggerFactory.getLogger(RequestProcessor::class.java)
    
    private val requestCache = mutableMapOf<String, RequestSummary>()
    
    suspend fun processRequest(request: Request?, operationKey: String?): RequestSummary {
        if (request == null) {
            return RequestSummary(
                requestId = "unknown",
                status = RequestStatus.Failed,
                message = "Request cannot be null",
                timestamp = Instant.now()
            )
        }
        
        val validation = request.validate()
        if (validation is Invalid) {
            return handleValidationFailure(request, validation)
        }
        
        if (!request.canBeProcessed()) {
            return RequestSummary(
                requestId = request.id,
                status = RequestStatus.Rejected("Request cannot be processed in current state"),
                message = validation.toString(),
                timestamp = Instant.now()
            )
        }
        
        val idempotencyKey = operationKey ?: request.id
        val idempotencyResult = validateIdempotency(idempotencyKey, request.id)
        
        if (!idempotencyResult.isNew) {
            logger.info("Returning cached result for idempotency key: $idempotencyKey")
            return buildSummaryFromCachedKey(idempotencyResult.existingKey!!)
        }
        
        val processingRequest = request.markAsProcessing()
        
        try {
            val clientInfo = fetchExternalInfo(processingRequest.clientInfo)
            
            val completedRequest = processingRequest.markAsCompleted()
            
            updateRequestInIdempotency(idempotencyKey, completedRequest)
            
            emitAuditEvent(completedRequest, "REQUEST_COMPLETED")
            
            val summary = RequestSummary(
                requestId = completedRequest.id,
                status = completedRequest.status,
                message = "Request processed successfully",
                timestamp = Instant.now()
            )
            
            requestCache[completedRequest.id] = summary
            
            return summary
        } catch (e: Exception) {
            return handleProcessingError(request, e)
        }
    }
    
    suspend fun getRequestStatus(requestId: String): RequestSummary? {
        return requestCache[requestId]
    }
    
    private suspend fun validateIdempotency(operationKey: String, requestId: String): IdempotencyResult {
        return withContext(Dispatchers.IO) {
            idempotencyRepository.validateAndMark(operationKey, requestId)
        }
    }
    
    private suspend fun fetchExternalInfo(clientInfo: ClientInfo): Map<String, Any> {
        return withContext(Dispatchers.IO) {
            try {
                val clientInfoMap = externalInfoClient.getClientInfo(
                    clientType = clientInfo.type.name,
                    clientId = clientInfo.id
                )
                clientInfoMap ?: emptyMap()
            } catch (e: Exception) {
                logger.warn("Failed to fetch external info: ${e.message}")
                emptyMap()
            }
        }
    }
    
    private suspend fun updateRequestInIdempotency(operationKey: String, request: Request) {
        withContext(Dispatchers.IO) {
            val key = idempotencyRepository.getKey(operationKey)
            if (key != null) {
                val updatedKey = key.markAsUsed(request.id)
                idempotencyRepository.saveKey(updatedKey)
            }
        }
    }
    
    private suspend fun markIdempotencyAsConsumed(operationKey: String, requestId: String) {
        withContext(Dispatchers.IO) {
            idempotencyRepository.markAsConsumed(operationKey)
        }
    }
    
    private suspend fun handleValidationFailure(
        request: Request,
        validation: Invalid
    ): RequestSummary {
        logger.warn("Validation failed for request ${request.id}: ${validation.reason}")
        
        emitAuditEvent(request, "VALIDATION_FAILED")
        
        return RequestSummary(
            requestId = request.id,
            status = RequestStatus.Rejected(validation.reason),
            message = validation.reason,
            timestamp = Instant.now()
        )
    }
    
    private suspend fun handleProcessingError(
        request: Request,
        error: Exception
    ): RequestSummary {
        logger.error("Error processing request ${request.id}: ${error.message}", error)
        
        emitAuditEvent(request, "PROCESSING_ERROR")
        
        return RequestSummary(
            requestId = request.id,
            status = RequestStatus.Failed,
            message = "Error processing request: ${error.message}",
            timestamp = Instant.now()
        )
    }
    
    private suspend fun emitAuditEvent(request: Request, eventType: String) {
        try {
            auditService.emitEvent(
                requestId = request.id,
                eventType = eventType,
                clientId = request.clientId,
                details = mapOf(
                    "clientType" to request.clientType.name,
                    "operationKey" to request.operationKey
                )
            )
        } catch (e: Exception) {
            logger.warn("Failed to emit audit event: ${e.message}")
        }
    }
    
    private fun buildSummaryFromCachedKey(operationKey: OperationKey): RequestSummary {
        val cachedRequestId = operationKey.processedRequestId ?: "unknown"
        
        val summary = RequestSummary(
            requestId = cachedRequestId,
            status = mapRequestStatusToKeyStatus(operationKey.status),
            message = "Result from previous request",
            timestamp = operationKey.createdAt
        )
        
        requestCache[cachedRequestId] = summary
        
        return summary
    }
    
    private fun mapRequestStatusToKeyStatus(keyStatus: OperationKeyStatus): RequestStatus {
        return when (keyStatus) {
            OperationKeyStatus.AVAILABLE -> RequestStatus.Pending
            OperationKeyStatus.CONSUMED -> RequestStatus.Completed
            OperationKeyStatus.EXPIRED -> RequestStatus.Failed
            OperationKeyStatus.EXHAUSTED -> RequestStatus.Failed
        }
    }
    
    private fun serializeRequest(request: Request): String {
        return "${request.id}|${request.clientId}|${request.operationKey}|${request.status}"
    }
    
    private fun deserializeRequest(data: String): Request? {
        return try {
            val parts = data.split("|")
            if (parts.size >= 4) {
                Request(
                    id = parts[0],
                    clientId = parts[1],
                    clientType = ClientType.STANDARD,
                    operationKey = parts[2],
                    requestData = "",
                    priority = 0,
                    status = RequestStatus.Pending
                )
            } else null
        } catch (e: Exception) {
            null
        }
    }
}