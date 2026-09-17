package com.pragma.microservice.infrastructure.exception

import com.pragma.microservice.domain.Invalid
import com.pragma.microservice.domain.Request
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.plugins.requestvalidation.RequestValidationException
import io.ktor.server.plugins.statuspages.StatusPagesConfig
import io.ktor.server.plugins.statuspages.StatusPagesPlugin
import io.ktor.server.request.path
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.Serializable
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.FileNotFoundException
import java.io.IOException
import java.util.NoSuchElementException

private val logger: Logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

@Serializable
data class ErrorResponse(
    val status: Int,
    val error: String,
    val message: String,
    val path: String,
    val timestamp: Long = System.currentTimeMillis(),
    val details: String? = null
)

sealed class DomainException(
    message: String,
    val statusCode: HttpStatusCode,
    val errorCode: String
) : Exception(message)

class ValidationException(
    message: String,
    val validationDetails: List<String> = emptyList()
) : DomainException(message, HttpStatusCode.BadRequest, "VALIDATION_ERROR")

class IdempotencyConflictException(
    message: String,
    val existingRequestId: String? = null
) : DomainException(message, HttpStatusCode.Conflict, "IDEMPOTENCY_CONFLICT")

class ProcessingException(
    message: String,
    val retryable: Boolean = true
) : DomainException(message, HttpStatusCode.InternalServerError, "PROCESSING_ERROR")

class ExternalServiceException(
    message: String,
    val serviceName: String,
    val retryable: Boolean = true
) : DomainException(message, HttpStatusCode.ServiceUnavailable, "EXTERNAL_SERVICE_UNAVAILABLE")

class NotFoundException(
    message: String,
    val resourceType: String
) : DomainException(message, HttpStatusCode.NotFound, "RESOURCE_NOT_FOUND")

class RateLimitExceededException(
    message: String,
    val retryAfterSeconds: Int = 60
) : DomainException(message, HttpStatusCode.TooManyRequests, "RATE_LIMIT_EXCEEDED")

fun StatusPagesPlugin.Config.setupGlobalExceptionHandler() {
    val log = LoggerFactory.getLogger("ExceptionHandler")
    
    exception<DomainException> { call, cause ->
        log.error("Domain exception: ${cause.errorCode} - ${cause.message}", cause)
        
        val response = ErrorResponse(
            status = cause.statusCode.value,
            error = cause.errorCode,
            message = cause.message ?: "An error occurred",
            path = call.request.path()
        )
        
        call.respond(cause.statusCode, response)
    }
    
    exception<RequestValidationException> { call, cause ->
        log.warn("Request validation failed: ${cause.message}")
        
        val errors = cause.reasons.joinToString("; ") { reason ->
            "${reason.type}: ${reason.message}"
        }
        
        val response = ErrorResponse(
            status = HttpStatusCode.BadRequest.value,
            error = "REQUEST_VALIDATION_ERROR",
            message = errors,
            path = call.request.path()
        )
        
        call.respond(HttpStatusCode.BadRequest, response)
    }
    
    exception<IllegalArgumentException> { call, cause ->
        log.warn("Illegal argument: ${cause.message}")
        
        val response = ErrorResponse(
            status = HttpStatusCode.BadRequest.value,
            error = "INVALID_ARGUMENT",
            message = cause.message ?: "Invalid argument provided",
            path = call.request.path()
        )
        
        call.respond(HttpStatusCode.BadRequest, response)
    }
    
    exception<IllegalStateException> { call, cause ->
        log.error("Illegal state: ${cause.message}", cause)
        
        val response = ErrorResponse(
            status = HttpStatusCode.Conflict.value,
            error = "INVALID_STATE",
            message = cause.message ?: "Invalid state",
            path = call.request.path()
        )
        
        call.respond(HttpStatusCode.Conflict, response)
    }
    
    exception<FileNotFoundException> { call, cause ->
        log.warn("File not found: ${cause.message}")
        
        val response = ErrorResponse(
            status = HttpStatusCode.NotFound.value,
            error = "FILE_NOT_FOUND",
            message = cause.message ?: "Resource not found",
            path = call.request.path()
        )
        
        call.respond(HttpStatusCode.NotFound, response)
    }
    
    exception<NoSuchElementException> { call, cause ->
        log.warn("Element not found: ${cause.message}")
        
        val response = ErrorResponse(
            status = HttpStatusCode.NotFound.value,
            error = "ELEMENT_NOT_FOUND",
            message = cause.message ?: "Required element not found",
            path = call.request.path()
        )
        
        call.respond(HttpStatusCode.NotFound, response)
    }
    
    exception<IOException> { call, cause ->
        log.error("IO error: ${cause.message}", cause)
        
        val response = ErrorResponse(
            status = HttpStatusCode.ServiceUnavailable.value,
            error = "IO_ERROR",
            message = "An I/O error occurred. Please try again later.",
            path = call.request.path()
        )
        
        call.respond(HttpStatusCode.ServiceUnavailable, response)
    }
    
    exception<CancellationException> { call, cause ->
        log.warn("Request cancelled: ${cause.message}")
        
        val response = ErrorResponse(
            status = HttpStatusCode.GatewayTimeout.value,
            error = "REQUEST_CANCELLED",
            message = "The request was cancelled",
            path = call.request.path()
        )
        
        call.respond(HttpStatusCode.GatewayTimeout, response)
    }
    
    exception<Exception> { call, cause ->
        log.error("Unhandled exception: ${cause.message}", cause)
        
        val response = ErrorResponse(
            status = HttpStatusCode.InternalServerError.value,
            error = "INTERNAL_SERVER_ERROR",
            message = "An unexpected error occurred. Please contact support.",
            path = call.request.path()
        )
        
        call.respond(HttpStatusCode.InternalServerError, response)
    }
    
    status(HttpStatusCode.NotFound) { call, status ->
        log.warn("404 Not Found: ${call.request.path()}")
        
        val response = ErrorResponse(
            status = status.value,
            error = "NOT_FOUND",
            message = "The requested resource was not found",
            path = call.request.path()
        )
        
        call.respond(status, response)
    }
    
    status(HttpStatusCode.MethodNotAllowed) { call, status ->
        log.warn("405 Method Not Allowed: ${call.request.path()}")
        
        val response = ErrorResponse(
            status = status.value,
            error = "METHOD_NOT_ALLOWED",
            message = "The HTTP method is not allowed for this endpoint",
            path = call.request.path()
        )
        
        call.respond(status, response)
    }
    
    status(HttpStatusCode.UnsupportedMediaType) { call, status ->
        log.warn("415 Unsupported Media Type: ${call.request.path()}")
        
        val response = ErrorResponse(
            status = status.value,
            error = "UNSUPPORTED_MEDIA_TYPE",
            message = "The request content type is not supported",
            path = call.request.path()
        )
        
        call.respond(status, response)
    }
}