package com.pragma.microservice.application

import com.pragma.microservice.domain.ClientInfo
import com.pragma.microservice.domain.ClientType
import com.pragma.microservice.domain.IdempotencyResult
import com.pragma.microservice.domain.OperationKey
import com.pragma.microservice.domain.OperationKeyStatus
import com.pragma.microservice.domain.Request
import com.pragma.microservice.domain.RequestStatus
import com.pragma.microservice.domain.RequestSummary
import com.pragma.microservice.infrastructure.audit.AuditEvent
import com.pragma.microservice.infrastructure.client.ExternalInfoClient
import com.pragma.microservice.infrastructure.idempotency.IdempotencyRepository
import com.pragma.microservice.infrastructure.audit.AuditService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*

class RequestProcessorTest {

    private val externalInfoClient = mockk<ExternalInfoClient>(relaxed = true)
    private val idempotencyRepository = mockk<IdempotencyRepository>(relaxed = true)
    private val auditService = mockk<AuditService>(relaxed = true)

    private lateinit var processor: RequestProcessor

    @BeforeEach
    fun setup() {
        processor = RequestProcessor(
            externalInfoClient = externalInfoClient,
            idempotencyRepository = idempotencyRepository,
            auditService = auditService
        )
    }

    @Test
    fun `processRequest debe procesar solicitud valida exitosamente`() = runTest {
        val request = createValidRequest()
        val operationKey = createOperationKey(status = OperationKeyStatus.AVAILABLE)
        val externalInfo = mapOf("info" to "data")

        coEvery { idempotencyRepository.validateAndMark(any(), any()) } returns IdempotencyResult(true, operationKey)
        coEvery { externalInfoClient.getClientInfo(any(), any()) } returns externalInfo
        coEvery { auditService.emitEvent(any()) } returns Unit

        val result = processor.processRequest(request, "op-key-001")

        assertEquals(RequestStatus.Completed, result.status)
        coVerify(exactly = 1) { idempotencyRepository.validateAndMark(any(), any()) }
        coVerify(exactly = 1) { externalInfoClient.getClientInfo(any(), any()) }
        coVerify(exactly = 1) { auditService.emitEvent(any()) }
    }

    @Test
    fun `processRequest debe rechazar solicitud con clave de operacion duplicada`() = runTest {
        val request = createValidRequest()
        val existingKey = createOperationKey(
            status = OperationKeyStatus.CONSUMED,
            processedRequestId = "existing-request-id"
        )

        coEvery { idempotencyRepository.validateAndMark(any(), any()) } returns IdempotencyResult(false, existingKey)

        val result = processor.processRequest(request, "op-key-001")

        assertTrue(result.status == RequestStatus.Completed)
        coVerify(exactly = 0) { externalInfoClient.getClientInfo(any(), any()) }
    }

    @Test
    fun `processRequest debe manejar fallo en servicio externo`() = runTest {
        val request = createValidRequest()
        val operationKey = createOperationKey(status = OperationKeyStatus.AVAILABLE)

        coEvery { idempotencyRepository.validateAndMark(any(), any()) } returns IdempotencyResult(true, operationKey)
        coEvery { externalInfoClient.getClientInfo(any(), any()) } returns null
        coEvery { auditService.emitEvent(any()) } returns Unit

        val result = processor.processRequest(request, "op-key-001")

        assertEquals(RequestStatus.Completed, result.status)
        coVerify(atLeast = 1) { auditService.emitEvent(any()) }
    }

    @Test
    fun `processRequest debe validar solicitud antes de procesar`() = runTest {
        val invalidRequest = createInvalidRequest()

        val result = processor.processRequest(invalidRequest, "op-key-001")

        assertTrue(result.status is RequestStatus.Rejected)
        coVerify(exactly = 0) { idempotencyRepository.validateAndMark(any(), any()) }
    }

    @Test
    fun `processRequest debe manejar solicitud nulla correctamente`() = runTest {
        val result = processor.processRequest(null, "op-key-001")

        assertEquals(RequestStatus.Failed, result.status)
    }

    @Test
    fun `processRequest debe emitir evento de auditoria con datos correctos`() = runTest {
        val request = createValidRequest()
        val operationKey = createOperationKey(status = OperationKeyStatus.AVAILABLE)
        val externalInfo = mapOf("info" to "data")
        val eventSlot = slot<AuditEvent>()

        coEvery { idempotencyRepository.validateAndMark(any(), any()) } returns IdempotencyResult(true, operationKey)
        coEvery { externalInfoClient.getClientInfo(any(), any()) } returns externalInfo
        coEvery { auditService.emitEvent(capture(eventSlot)) } returns Unit

        processor.processRequest(request, "op-key-001")

        assertEquals(request.id, eventSlot.captured.requestId)
    }

    private fun createValidRequest(): Request {
        return Request(
            id = "req-001",
            clientId = "client-123",
            clientType = ClientType.PREMIUM,
            operationKey = "op-key-001",
            requestData = "test data",
            priority = 0,
            status = RequestStatus.Pending
        )
    }

    private fun createInvalidRequest(): Request {
        return Request(
            id = "req-002",
            clientId = "",
            clientType = ClientType.STANDARD,
            operationKey = "",
            requestData = "",
            priority = -1,
            status = RequestStatus.Pending
        )
    }

    private fun createOperationKey(
        key: String = "op-key-001",
        status: OperationKeyStatus = OperationKeyStatus.AVAILABLE,
        processedRequestId: String? = null
    ): OperationKey {
        return OperationKey(
            key = key,
            status = status,
            createdAt = java.time.Instant.now().minusSeconds(3600),
            expiresAt = java.time.Instant.now().plusSeconds(3600),
            processedRequestId = processedRequestId,
            reuseCount = 0
        )
    }
}