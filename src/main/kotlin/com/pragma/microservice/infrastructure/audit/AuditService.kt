package com.pragma.microservice.infrastructure.audit

import com.pragma.microservice.domain.ClientInfo
import com.pragma.microservice.domain.RequestStatus
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Instant

class AuditService : KoinComponent {

    private val httpClient: HttpClient by inject()

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(AuditService::class.java)
        private const val AUDIT_SYSTEM_URL = "http://audit-system:8081/api/v1/events"
        private const val BATCH_SIZE = 100
        private const val MAX_RETRIES = 3
    }

    private val eventBuffer = mutableListOf<AuditEvent>()
    private var lastFlushTime = Instant.now()

    suspend fun emitEvent(
        eventType: String,
        requestId: String,
        status: String,
        clientInfo: ClientInfo
    ) {
        val event = AuditEvent(
            eventId = generateEventId(),
            eventType = eventType,
            requestId = requestId,
            timestamp = Instant.now(),
            status = status,
            clientId = clientInfo.clientId,
            clientType = clientInfo.clientType.name,
            metadata = buildMetadata(clientInfo)
        )

        synchronized(eventBuffer) {
            eventBuffer.add(event)
            if (eventBuffer.size >= BATCH_SIZE || shouldFlush()) {
                flushEvents()
            }
        }

        logger.debug("Audit event emitted: type=$eventType, requestId=$requestId")
    }

    suspend fun emitEventSync(
        eventType: String,
        requestId: String,
        status: String,
        clientInfo: ClientInfo
    ) {
        val event = AuditEvent(
            eventId = generateEventId(),
            eventType = eventType,
            requestId = requestId,
            timestamp = Instant.now(),
            status = status,
            clientId = clientInfo.clientId,
            clientType = clientInfo.clientType.name,
            metadata = buildMetadata(clientInfo)
        )

        sendEventToAuditSystem(event)
        logger.info("Audit event sent synchronously: type=$eventType, requestId=$requestId")
    }

    suspend fun flushEvents() {
        val eventsToFlush: List<AuditEvent>
        synchronized(eventBuffer) {
            if (eventBuffer.isEmpty()) return
            eventsToFlush = eventBuffer.toList()
            eventBuffer.clear()
            lastFlushTime = Instant.now()
        }

        try {
            sendBatchToAuditSystem(eventsToFlush)
            logger.info("Flushed ${eventsToFlush.size} audit events")
        } catch (e: Exception) {
            logger.error("Error flushing audit events: ${e.message}", e)
            synchronized(eventBuffer) {
                eventBuffer.addAll(0, eventsToFlush)
            }
        }
    }

    private suspend fun sendEventToAuditSystem(event: AuditEvent) {
        withContext(Dispatchers.IO) {
            var retries = 0
            var lastException: Exception? = null

            while (retries < MAX_RETRIES) {
                try {
                    val response = httpClient.post(AUDIT_SYSTEM_URL) {
                        contentType(ContentType.Application.Json)
                        setBody(event)
                    }

                    if (response.status.value in 200..299) {
                        logger.debug("Event ${event.eventId} sent successfully")
                        return@withContext
                    } else {
                        logger.warn("Audit system returned status: ${response.status.value}")
                    }
                } catch (e: Exception) {
                    lastException = e
                    logger.warn("Error sending audit event (attempt ${retries + 1}): ${e.message}")
                }
                retries++
                if (retries < MAX_RETRIES) {
                    delay(retries * 1000L)
                }
            }

            logger.error("Failed to send audit event after $MAX_RETRIES attempts", lastException)
        }
    }

    private suspend fun sendBatchToAuditSystem(events: List<AuditEvent>) {
        withContext(Dispatchers.IO) {
            var retries = 0
            var lastException: Exception? = null

            while (retries < MAX_RETRIES) {
                try {
                    val batchRequest = AuditBatchRequest(
                        batchId = generateBatchId(),
                        events = events,
                        timestamp = Instant.now()
                    )

                    val response = httpClient.post("$AUDIT_SYSTEM_URL/batch") {
                        contentType(ContentType.Application.Json)
                        setBody(batchRequest)
                    }

                    if (response.status.value in 200..299) {
                        logger.debug("Batch ${batchRequest.batchId} sent successfully")
                        return@withContext
                    } else {
                        logger.warn("Audit system batch endpoint returned status: ${response.status.value}")
                    }
                } catch (e: Exception) {
                    lastException = e
                    logger.warn("Error sending audit batch (attempt ${retries + 1}): ${e.message}")
                }
                retries++
                if (retries < MAX_RETRIES) {
                    delay(retries * 1000L)
                }
            }

            for (event in events) {
                sendEventToAuditSystem(event)
            }
            logger.error("Failed to send audit batch after $MAX_RETRIES attempts, falling back to individual events", lastException)
        }
    }

    private fun shouldFlush(): Boolean {
        val timeSinceLastFlush = Instant.now().epochSecond - lastFlushTime.epochSecond
        return timeSinceLastFlush > 60
    }

    private fun generateEventId(): String {
        return "evt_${System.currentTimeMillis()}_${(Math.random() * 10000).toLong()}"
    }

    private fun generateBatchId(): String {
        return "batch_${System.currentTimeMillis()}_${(Math.random() * 10000).toLong()}"
    }

    private fun buildMetadata(clientInfo: ClientInfo): Map<String, String> {
        return mapOf(
            "clientType" to clientInfo.clientType.name,
            "clientId" to clientInfo.clientId,
            "origin" to "request-processor",
            "service" to "microservice"
        )
    }

    private suspend fun delay(millis: Long) {
        kotlinx.coroutines.delay(millis)
    }
}

data class AuditEvent(
    val eventId: String,
    val eventType: String,
    val requestId: String,
    val timestamp: Instant,
    val status: String,
    val clientId: String,
    val clientType: String,
    val metadata: Map<String, String>
)

data class AuditBatchRequest(
    val batchId: String,
    val events: List<AuditEvent>,
    val timestamp: Instant
)