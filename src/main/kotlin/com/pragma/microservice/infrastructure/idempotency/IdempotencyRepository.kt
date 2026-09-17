package com.pragma.microservice.infrastructure.idempotency

import com.pragma.microservice.domain.IdempotencyResult
import com.pragma.microservice.domain.OperationKey
import com.pragma.microservice.domain.OperationKeyStatus
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.time.Instant

class IdempotencyRepository(
    private val storagePath: String = "./data/idempotency"
) {
    private val mutex = Mutex()
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    private val storageDir: File by lazy {
        File(storagePath).also { it.mkdirs() }
    }

    suspend fun saveKey(operationKey: OperationKey): IdempotencyResult = mutex.withLock {
        val keyFile = getKeyFile(operationKey.key)

        if (keyFile.exists()) {
            val existingKey = loadKey(operationKey.key)
            if (existingKey != null) {
                return@withLock IdempotencyResult(
                    isNew = false,
                    operationKey = existingKey,
                    existingRequestId = existingKey.originalRequestId
                )
            }
        }

        val keyToSave = operationKey.copy(status = OperationKeyStatus.USED)
        persistKey(keyToSave)

        IdempotencyResult(
            isNew = true,
            operationKey = keyToSave
        )
    }

    suspend fun getKey(operationKey: String): OperationKey? = mutex.withLock {
        loadKey(operationKey)
    }

    suspend fun markAsConsumed(operationKey: String): Boolean = mutex.withLock {
        val existingKey = loadKey(operationKey) ?: return@withLock false
        val updatedKey = existingKey.copy(
            status = OperationKeyStatus.CONSUMED,
            usedAt = Instant.now()
        )
        persistKey(updatedKey)
        true
    }

    suspend fun exists(operationKey: String): Boolean = mutex.withLock {
        val keyFile = getKeyFile(operationKey)
        keyFile.exists() && loadKey(operationKey)?.validateForProcessing() == true
    }

    suspend fun validateAndMark(operationKey: String, requestId: String): IdempotencyResult = mutex.withLock {
        val existingKey = loadKey(operationKey)

        if (existingKey == null) {
            val newKey = OperationKey(
                key = operationKey,
                requestId = requestId,
                status = OperationKeyStatus.USED,
                originalRequestId = requestId
            )
            persistKey(newKey)
            return@withLock IdempotencyResult(
                isNew = true,
                operationKey = newKey
            )
        }

        if (!existingKey.validateForProcessing()) {
            return@withLock IdempotencyResult(
                isNew = false,
                operationKey = existingKey,
                existingRequestId = existingKey.originalRequestId
            )
        }

        val updatedKey = existingKey.markAsUsed(requestId)
        persistKey(updatedKey)

        IdempotencyResult(
            isNew = false,
            operationKey = updatedKey,
            existingRequestId = existingKey.originalRequestId
        )
    }

    suspend fun cleanupExpiredKeys(): Int = mutex.withLock {
        val keyFiles = storageDir.listFiles { file -> file.extension == "json" } ?: return@withLock 0
        var cleanedCount = 0

        keyFiles.forEach { file ->
            try {
                val key = json.decodeFromString<OperationKey>(file.readText())
                if (key.isExpired()) {
                    val expiredKey = key.markAsExpired()
                    persistKey(expiredKey)
                    cleanedCount++
                }
            } catch (e: Exception) {
                // Skip invalid files
            }
        }

        cleanedCount
    }

    private fun getKeyFile(operationKey: String): File {
        val safeFileName = operationKey.replace(Regex("[^A-Za-z0-9_-]"), "_")
        return File(storageDir, "$safeFileName.json")
    }

    private fun loadKey(operationKey: String): OperationKey? {
        val keyFile = getKeyFile(operationKey)
        if (!keyFile.exists()) return null

        return try {
            json.decodeFromString<OperationKey>(keyFile.readText())
        } catch (e: Exception) {
            null
        }
    }

    private fun persistKey(operationKey: OperationKey) {
        val keyFile = getKeyFile(operationKey.key)
        keyFile.writeText(json.encodeToString(operationKey))
    }
}

suspend fun main() {
    val repository = IdempotencyRepository()
    
    // Test basic operations
    val testKey = "test-operation-key-123"
    val result1 = repository.validateAndMark(testKey, "request-1")
    println("First call - isNew: ${result1.isNew}, requestId: ${result1.operationKey.requestId}")
    
    val result2 = repository.validateAndMark(testKey, "request-2")
    println("Second call - isNew: ${result2.isNew}, existingId: ${result2.existingRequestId}")
    
    val exists = repository.exists(testKey)
    println("Key exists: $exists")
    
    val retrieved = repository.getKey(testKey)
    println("Retrieved key: ${retrieved?.status}")
}