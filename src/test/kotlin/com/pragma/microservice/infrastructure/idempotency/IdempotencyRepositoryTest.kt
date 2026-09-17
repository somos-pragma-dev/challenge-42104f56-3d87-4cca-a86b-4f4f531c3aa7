package com.pragma.microservice.infrastructure.idempotency

import com.pragma.microservice.domain.*
import io.mockk.*
import io.mockk.coEvery
import io.mockk.coVerify
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.io.File
import java.time.Instant

class IdempotencyRepositoryTest {

    private val testDir = File("build/test-idempotency")
    private lateinit var repository: IdempotencyRepository

    @BeforeEach
    fun setup() {
        testDir.mkdirs()
        mockkStatic("kotlin.io.FilesKt")
        every { File(any<String>()).exists() } returns false
        every { File(any<String>()).mkdirs() } returns true
        every { File(any<String>()).parentFile } returns testDir
        every { File(any<String>()).writeText(any()) } returns Unit
        every { File(any<String>()).readText() } returns ""
        repository = IdempotencyRepository(testDir)
    }

    @AfterEach
    fun cleanup() {
        unmockkAll()
        testDir.deleteRecursively()
    }

    @Test
    fun `saveKey debe guardar clave de operacion exitosamente`() = runTest {
        val operationKey = createOperationKey()

        val result = repository.saveKey(operationKey)

        assertTrue(result.isNew)
        assertNotNull(result.key)
    }

    @Test
    fun `saveKey debe retornar clave existente cuando ya existe`() = runTest {
        val operationKey = createOperationKey()
        repository.saveKey(operationKey)

        val result = repository.saveKey(operationKey)

        assertFalse(result.isNew)
        assertNotNull(result.key)
    }

    @Test
    fun `getKey debe retornar clave existente`() = runTest {
        val operationKey = createOperationKey()
        repository.saveKey(operationKey)

        val retrieved = repository.getKey(operationKey.key)

        assertNotNull(retrieved)
        assertEquals(operationKey.key, retrieved?.key)
    }

    @Test
    fun `getKey debe retornar null para clave inexistente`() = runTest {
        val retrieved = repository.getKey("nonexistent-key")

        assertNull(retrieved)
    }

    @Test
    fun `exists debe retornar true para clave existente`() = runTest {
        val operationKey = createOperationKey()
        repository.saveKey(operationKey)

        val exists = repository.exists(operationKey.key)

        assertTrue(exists)
    }

    @Test
    fun `exists debe retornar false para clave inexistente`() = runTest {
        val exists = repository.exists("nonexistent-key")

        assertFalse(exists)
    }

    @Test
    fun `markAsConsumed debe marcar clave como consumida`() = runTest {
        val operationKey = createOperationKey()
        repository.saveKey(operationKey)

        val result = repository.markAsConsumed(operationKey.key)

        assertTrue(result)
    }

    @Test
    fun `markAsConsumed debe retornar false para clave inexistente`() = runTest {
        val result = repository.markAsConsumed("nonexistent-key")

        assertFalse(result)
    }

    @Test
    fun `validateAndMark debe crear nueva clave si no existe`() = runTest {
        val result = repository.validateAndMark("new-key", "request-123")

        assertTrue(result.isNew)
        assertNotNull(result.key)
        assertEquals(OperationKeyStatus.CONSUMED, result.key?.status)
    }

    @Test
    fun `validateAndMark debe rechazar clave ya consumida`() = runTest {
        val operationKey = createOperationKey(status = OperationKeyStatus.CONSUMED, processedRequestId = "existing-req")
        repository.saveKey(operationKey)

        val result = repository.validateAndMark(operationKey.key, "new-request")

        assertFalse(result.isNew)
        assertEquals("existing-req", result.key?.processedRequestId)
    }

    @Test
    fun `validateAndMark debe permitir reutilizacion de clave expirada`() = runTest {
        val expiredKey = createOperationKey(
            status = OperationKeyStatus.EXPIRED,
            expiresAt = Instant.now().minusSeconds(100)
        )
        repository.saveKey(expiredKey)

        val result = repository.validateAndMark(expiredKey.key, "new-request")

        assertTrue(result.isNew)
    }

    @Test
    fun `cleanupExpiredKeys debe eliminar claves expiradas`() = runTest {
        val expiredKey = createOperationKey(
            key = "expired-key",
            expiresAt = Instant.now().minusSeconds(100)
        )
        repository.saveKey(expiredKey)
        repository.saveKey(createOperationKey(key = "valid-key"))

        val cleanedCount = repository.cleanupExpiredKeys()

        assertTrue(cleanedCount > 0)
    }

    @Test
    fun `cleanupExpiredKeys debe retornar cero cuando no hay claves expiradas`() = runTest {
        repository.saveKey(createOperationKey())

        val cleanedCount = repository.cleanupExpiredKeys()

        assertEquals(0, cleanedCount)
    }

    @Test
    fun `OperationKey markAsUsed debe actualizar estado correctamente`() = runTest {
        val key = createOperationKey()

        val updatedKey = key.markAsUsed("new-request-id")

        assertEquals(OperationKeyStatus.CONSUMED, updatedKey.status)
        assertEquals("new-request-id", updatedKey.processedRequestId)
    }

    @Test
    fun `OperationKey markAsExpired debe cambiar estado a expirado`() = runTest {
        val key = createOperationKey()

        val expiredKey = key.markAsExpired()

        assertEquals(OperationKeyStatus.EXPIRED, expiredKey.status)
    }

    @Test
    fun `OperationKey isExpired debe retornar true para clave expirada`() = runTest {
        val expiredKey = createOperationKey(
            status = OperationKeyStatus.EXPIRED,
            expiresAt = Instant.now().minusSeconds(100)
        )

        assertTrue(expiredKey.isExpired())
    }

    @Test
    fun `OperationKey isUsed debe retornar true para clave consumida`() = runTest {
        val consumedKey = createOperationKey(status = OperationKeyStatus.CONSUMED)

        assertTrue(consumedKey.isUsed())
    }

    private fun createOperationKey(
        key: String = "test-key-001",
        status: OperationKeyStatus = OperationKeyStatus.AVAILABLE,
        processedRequestId: String? = null,
        expiresAt: Instant = Instant.now().plusSeconds(3600)
    ): OperationKey {
        return OperationKey(
            key = key,
            status = status,
            createdAt = Instant.now().minusSeconds(3600),
            expiresAt = expiresAt,
            processedRequestId = processedRequestId,
            reuseCount = 0
        )
    }
}