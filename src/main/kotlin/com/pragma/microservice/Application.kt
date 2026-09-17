package com.pragma.microservice

import com.pragma.microservice.application.RequestProcessor
import com.pragma.microservice.infrastructure.MicrosservicioController
import com.pragma.microservice.infrastructure.audit.AuditService
import com.pragma.microservice.infrastructure.client.ExternalInfoClient
import com.pragma.microservice.infrastructure.config.ResilienceConfiguration
import com.pragma.microservice.infrastructure.exception.GlobalExceptionHandler
import com.pragma.microservice.infrastructure.idempotency.IdempotencyRepository
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respondText
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

fun main() {
    val logger = LoggerFactory.getLogger("Application")
    logger.info("Starting microservice application...")
    
    val json = Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
        coerceInputValues = true
    }
    
    val idempotencyRepository = IdempotencyRepository()
    val externalInfoClient = ExternalInfoClient()
    val resilienceConfig = ResilienceConfiguration()
    val auditService = AuditService()
    
    val requestProcessor = RequestProcessor(
        externalInfoClient = externalInfoClient,
        idempotencyRepository = idempotencyRepository,
        auditService = auditService,
        circuitBreaker = resilienceConfig.circuitBreaker,
        retry = resilienceConfig.retry,
        bulkhead = resilienceConfig.bulkhead
    )
    
    val controller = MicrosservicioController(requestProcessor)
    
    val server = embeddedServer(Netty, port = 8080) {
        install(ContentNegotiation) {
            json(json)
        }
        
        install(CallLogging) {
            level = ch.qos.logback.classic.Level.INFO
        }
        
        install(StatusPages) {
            val exceptionHandler = GlobalExceptionHandler()
            exceptionHandler.configure(this)
        }
        
        controller.configureRoutes(this)
        
        logger.info("Ktor server configured successfully on port 8080")
    }
    
    Runtime.getRuntime().addShutdownHook(Thread {
        logger.info("Shutting down microservice...")
        server.stop(1000, 5000)
    })
    
    server.start(wait = true)
}