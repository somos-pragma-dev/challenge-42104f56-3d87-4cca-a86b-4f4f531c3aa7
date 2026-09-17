package com.pragma.microservice.infrastructure.client

import com.pragma.microservice.domain.Request
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.mockk.*
import io.mockk.coEvery
import io.mockk.coVerify
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*

class ExternalInfoClientTest {

    private val httpClient = mockk<HttpClient>(relaxed = true)
    private lateinit var client: ExternalInfoClient

    @BeforeEach
    fun setup() {
        client = ExternalInfoClient(httpClient)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `getClientInfo debe retornar informacion exitosa cuando el servicio responde correctamente`() = runTest {
        val clientId = "client-123"
        val clientType = "PREMIUM"
        val expectedResponse = mapOf(
            "id" to clientId,
            "type" to clientType,
            "name" to "Test Client",
            "additionalData" to mapOf("key" to "value")
        )

        coEvery {
            httpClient.get("${client.baseUrl}/client/$clientType/$clientId")
        } returns HttpResponseMock(expectedResponse)

        val result = client.getClientInfo(clientType, clientId)

        assertNotNull(result)
        assertEquals(clientId, result?.get("id"))
        coVerify(exactly = 1) { httpClient.get("${client.baseUrl}/client/$clientType/$clientId") }
    }

    @Test
    fun `getClientInfo debe manejar error 404 del servicio externo`() = runTest {
        val clientId = "nonexistent"
        val clientType = "STANDARD"

        coEvery {
            httpClient.get("${client.baseUrl}/client/$clientType/$clientId")
        } throws ExternalServiceException(
            "Resource not found",
            HttpStatusCode.NotFound.value
        )

        val result = client.getClientInfo(clientType, clientId)

        assertNull(result)
    }

    @Test
    fun `getClientInfo debe manejar error 500 del servicio externo`() = runTest {
        val clientId = "client-500"
        val clientType = "PREMIUM"

        coEvery {
            httpClient.get("${client.baseUrl}/client/$clientType/$clientId")
        } throws ExternalServiceException(
            "Internal server error",
            HttpStatusCode.InternalServerError.value
        )

        val result = client.getClientInfo(clientType, clientId)

        assertNull(result)
    }

    @Test
    fun `validateClient debe retornar true cuando el cliente es valido`() = runTest {
        val clientId = "valid-client"
        val clientType = "PREMIUM"

        coEvery {
            httpClient.get("${client.baseUrl}/validate/$clientType/$clientId")
        } returns HttpResponseMock(mapOf("valid" to true))

        val result = client.validateClient(clientType, clientId)

        assertTrue(result)
    }

    @Test
    fun `validateClient debe retornar false cuando el cliente es invalido`() = runTest {
        val clientId = "invalid-client"
        val clientType = "STANDARD"

        coEvery {
            httpClient.get("${client.baseUrl}/validate/$clientType/$clientId")
        } returns HttpResponseMock(mapOf("valid" to false))

        val result = client.validateClient(clientType, clientId)

        assertFalse(result)
    }

    @Test
    fun `getClientHistory debe retornar historial de operaciones`() = runTest {
        val clientId = "client-history"
        val expectedHistory = listOf(
            mapOf("operation" to "op-1", "timestamp" to "2024-01-01"),
            mapOf("operation" to "op-2", "timestamp" to "2024-01-02")
        )

        coEvery {
            httpClient.get("${client.baseUrl}/history/$clientId")
        } returns HttpResponseMock(expectedHistory)

        val result = client.getClientHistory(clientId)

        assertEquals(2, result.size)
    }

    @Test
    fun `getClientInfo debe lanzar exception para clientId vacio`() = runTest {
        val result = client.getClientInfo("PREMIUM", "")

        assertNull(result)
    }

    @Test
    fun `getClientInfo debe lanzar exception para clientType vacio`() = runTest {
        val result = client.getClientInfo("", "client-123")

        assertNull(result)
    }
}

class ExternalServiceException(message: String, val statusCode: Int) : Exception(message)

class HttpResponseMock<T>(private val body: T) : HttpResponse() {
    override val call: HttpRequest = mockk(relaxed = true)
    override val contentType: ContentType = ContentType.Application.Json
    override val headers: Headers = HeadersBuilder().apply { append("Content-Type", "application/json") }.build()
    override val status: HttpStatusCode = HttpStatusCode.OK
    override val version: HttpProtocolVersion = HttpProtocolVersion.HTTP_1_1

    override suspend fun body(): T = body
}