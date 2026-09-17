package com.pragma.microservice.domain

import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.UUID

sealed class RequestStatus {
    data object Pending : RequestStatus()
    data object Processing : RequestStatus()
    data object Completed : RequestStatus()
    data object Failed : RequestStatus()
    data class Rejected(val reason: String) : RequestStatus()
}

@Serializable
data class ClientInfo(
    val clientId: String,
    val clientName: String,
    val clientType: ClientType,
    val priority: Int = 0
)

@Serializable
enum class ClientType {
    STANDARD, PREMIUM, ENTERPRISE
}

@Serializable
data class Request(
    val id: String = UUID.randomUUID().toString(),
    val operationKey: String,
    val clientInfo: ClientInfo,
    val requestData: Map<String, String>,
    val status: RequestStatus = RequestStatus.Pending,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
    val retryCount: Int = 0
) {
    fun validate(): ValidationResult {
        if (operationKey.isBlank()) {
            return ValidationResult.Invalid("Operation key cannot be blank")
        }
        if (operationKey.length < 8) {
            return ValidationResult.Invalid("Operation key must be at least 8 characters")
        }
        if (clientInfo.clientId.isBlank()) {
            return ValidationResult.Invalid("Client ID cannot be blank")
        }
        if (requestData.isEmpty()) {
            return ValidationResult.Invalid("Request data cannot be empty")
        }
        if (!clientInfo.clientId.matches(Regex("^[A-Za-z0-9_-]+$"))) {
            return ValidationResult.Invalid("Client ID contains invalid characters")
        }
        return ValidationResult.Valid
    }

    fun canBeProcessed(): Boolean {
        return when (status) {
            is RequestStatus.Pending -> true
            is RequestStatus.Processing -> retryCount < MAX_RETRY_COUNT
            else -> false
        }
    }

    fun markAsProcessing(): Request {
        return copy(
            status = RequestStatus.Processing,
            updatedAt = Instant.now()
        )
    }

    fun markAsCompleted(): Request {
        return copy(
            status = RequestStatus.Completed,
            updatedAt = Instant.now()
        )
    }

    fun markAsFailed(): Request {
        return copy(
            status = RequestStatus.Failed,
            updatedAt = Instant.now(),
            retryCount = retryCount + 1
        )
    }

    fun reject(reason: String): Request {
        return copy(
            status = RequestStatus.Rejected(reason),
            updatedAt = Instant.now()
        )
    }

    companion object {
        const val MAX_RETRY_COUNT = 3
    }
}

sealed class ValidationResult {
    data object Valid : ValidationResult()
    data class Invalid(val reason: String) : ValidationResult()
}

fun Request.toSummary(): RequestSummary {
    return RequestSummary(
        id = id,
        operationKey = operationKey,
        clientId = clientInfo.clientId,
        status = status.toString(),
        createdAt = createdAt.toString()
    )
}

@Serializable
data class RequestSummary(
    val id: String,
    val operationKey: String,
    val clientId: String,
    val status: String,
    val createdAt: String
)