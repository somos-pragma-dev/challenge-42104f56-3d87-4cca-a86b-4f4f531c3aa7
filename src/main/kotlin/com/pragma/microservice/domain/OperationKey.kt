package com.pragma.microservice.domain

import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.UUID

@Serializable
data class OperationKey(
    val key: String,
    val requestId: String = UUID.randomUUID().toString(),
    val createdAt: Instant = Instant.now(),
    val usedAt: Instant? = null,
    val status: OperationKeyStatus = OperationKeyStatus.CREATED,
    val originalRequestId: String? = null
) {
    fun markAsUsed(processedRequestId: String): OperationKey {
        return copy(
            usedAt = Instant.now(),
            status = OperationKeyStatus.USED,
            originalRequestId = processedRequestId
        )
    }

    fun markAsExpired(): OperationKey {
        return copy(
            usedAt = Instant.now(),
            status = OperationKeyStatus.EXPIRED
        )
    }

    fun isExpired(): Boolean {
        val expirationDuration = java.time.Duration.ofHours(EXPIRATION_HOURS)
        return Instant.now().minus(expirationDuration).isAfter(createdAt)
    }

    fun isUsed(): Boolean {
        return status == OperationKeyStatus.USED
    }

    fun canBeReused(): Boolean {
        return status == OperationKeyStatus.USED && 
               originalRequestId != null && 
               !isExpired()
    }

    fun matches(pattern: Regex): Boolean {
        return key.matches(pattern)
    }

    fun validateForProcessing(): Boolean {
        return when {
            isExpired() -> false
            status == OperationKeyStatus.CONSUMED -> false
            status == OperationKeyStatus.EXPIRED -> false
            else -> true
        }
    }

    companion object {
        const val EXPIRATION_HOURS = 24L
        const val MIN_KEY_LENGTH = 8
        const val MAX_KEY_LENGTH = 128

        fun create(operationKey: String): OperationKey {
            require(operationKey.length in MIN_KEY_LENGTH..MAX_KEY_LENGTH) {
                "Operation key must be between $MIN_KEY_LENGTH and $MAX_KEY_LENGTH characters"
            }
            require(operationKey.matches(Regex("^[A-Za-z0-9_-]+$"))) {
                "Operation key contains invalid characters"
            }
            return OperationKey(key = operationKey)
        }

        fun fromRequest(request: Request): OperationKey {
            return create(request.operationKey)
        }
    }
}

@Serializable
enum class OperationKeyStatus {
    CREATED,
    USED,
    CONSUMED,
    EXPIRED
}

data class IdempotencyResult(
    val isNew: Boolean,
    val operationKey: OperationKey,
    val existingRequestId: String? = null
)