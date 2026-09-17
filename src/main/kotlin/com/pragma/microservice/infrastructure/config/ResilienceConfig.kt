package com.pragma.microservice.infrastructure.config

import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import io.github.resilience4j.retry.Retry
import io.github.resilience4j.retry.RetryConfig
import io.github.resilience4j.retry.RetryRegistry
import io.github.resilience4j.bulkhead.ThreadPoolBulkhead
import io.github.resilience4j.bulkhead.ThreadPoolBulkheadConfig
import io.github.resilience4j.bulkhead.ThreadPoolBulkheadRegistry
import io.github.resilience4j.kotlin.circuitbreaker.configure
import io.github.resilience4j.kotlin.retry.configure
import io.github.resilience4j.kotlin.bulkhead.configure
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStopping
import org.koin.dsl.module
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.concurrent.Executors

private val logger: Logger = LoggerFactory.getLogger(ResilienceConfig::class.java)

fun Application.configureResilience() {
    logger.info("Initializing Resilience4j configuration...")
    
    val circuitBreakerRegistry = createCircuitBreakerRegistry()
    val retryRegistry = createRetryRegistry()
    val bulkheadRegistry = createBulkheadRegistry()
    
    logger.info("CircuitBreaker configured with failure threshold: 50%, timeout: 5s")
    logger.info("Retry configured with max attempts: 3, wait duration: 1s")
    logger.info("Bulkhead configured with max concurrent calls: 100")
    
    environment.monitor.subscribe(ApplicationStopping) {
        logger.info("Shutting down Resilience4j resources...")
        circuitBreakerRegistry.allCircuitBreakers.forEach { it.close() }
        retryRegistry.allRetries.forEach { it.close() }
        bulkheadRegistry.allBulkheads.forEach { it.close() }
        logger.info("Resilience4j resources released")
    }
}

private fun createCircuitBreakerRegistry(): CircuitBreakerRegistry {
    val config = CircuitBreakerConfig.custom()
        .failureRateThreshold(50f)
        .slowCallRateThreshold(80f)
        .slowCallDurationThreshold(Duration.ofSeconds(3))
        .waitDurationInOpenState(Duration.ofSeconds(10))
        .permittedNumberOfCallsInHalfOpenState(3)
        .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
        .minimumNumberOfCalls(10)
        .maxWaitDurationInHalfOpenState(Duration.ofSeconds(5))
        .automaticTransitionFromOpenToHalfOpenEnabled(true)
        .recordExceptions(listOf(
            java.io.IOException::class.java,
            java.util.concurrent.TimeoutException::class.java,
            kotlinx.coroutines.CancellationException::class.java
        ))
        .ignoreExceptions(
            IllegalArgumentException::class.java,
            IllegalStateException::class.java
        )
        .build()
    
    return CircuitBreakerRegistry.of(config)
}

private fun createRetryRegistry(): RetryRegistry {
    val config = RetryConfig.custom()
        .maxAttempts(3)
        .waitDuration(Duration.ofSeconds(1))
        .retryExceptions(
            java.io.IOException::class.java,
            java.util.concurrent.TimeoutException::class.java,
            kotlinx.coroutines.CancellationException::class.java
        )
        .ignoreExceptions(
            IllegalArgumentException::class.java,
            IllegalStateException::class.java
        )
        .retryPredicates { throwable ->
            throwable is java.io.IOException || 
            throwable is java.util.concurrent.TimeoutException
        }
        .build()
    
    return RetryRegistry.of(config)
}

private fun createBulkheadRegistry(): ThreadPoolBulkheadRegistry {
    val config = ThreadPoolBulkheadConfig.custom()
        .maxThreadPoolSize(100)
        .coreThreadPoolSize(20)
        .keepAliveDuration(Duration.ofSeconds(30))
        .queueCapacity(200)
        .build()
    
    return ThreadPoolBulkheadRegistry.of(config)
}

val resilienceModule = module {
    single { createCircuitBreakerRegistry() }
    single { createRetryRegistry() }
    single { createBulkheadRegistry() }
    single { provideExecutorService() }
}

private fun provideExecutorService() = Executors.newFixedThreadPool(20)

fun CircuitBreakerRegistry.getCircuitBreaker(name: String): CircuitBreaker =
    getExistingCircuitBreaker(name) ?: apply {
        logger.warn("CircuitBreaker '$name' not found, creating with default config")
        io.github.resilience4j.circuitbreaker.CircuitBreaker.of(name, 
            CircuitBreakerConfig.custom().failureRateThreshold(50f).build()
        )
    }.let { getOrCreateCircuitBreaker(name) }

fun RetryRegistry.getRetry(name: String): Retry =
    getExistingRetry(name) ?: apply {
        logger.warn("Retry '$name' not found, creating with default config")
        io.github.resilience4j.retry.Retry.of(name, 
            RetryConfig.custom().maxAttempts(3).build()
        )
    }.let { getOrCreateRetry(name) }

fun ThreadPoolBulkheadRegistry.getBulkhead(name: String): ThreadPoolBulkhead =
    getExistingBulkhead(name) ?: apply {
        logger.warn("Bulkhead '$name' not found, creating with default config")
        io.github.resilience4j.bulkhead.ThreadPoolBulkhead.of(name, 
            ThreadPoolBulkheadConfig.custom().maxThreadPoolSize(50).build()
        )
    }.let { getOrCreateBulkhead(name) }

class ResilienceConfiguration(
    private val circuitBreakerRegistry: CircuitBreakerRegistry,
    private val retryRegistry: RetryRegistry,
    private val bulkheadRegistry: ThreadPoolBulkheadRegistry,
    private val executorService: java.util.concurrent.ExecutorService
) {
    fun getCircuitBreaker(name: String): CircuitBreaker =
        circuitBreakerRegistry.getCircuitBreaker(name)
    
    fun getRetry(name: String): Retry =
        retryRegistry.getRetry(name)
    
    fun getBulkhead(name: String): ThreadPoolBulkhead =
        bulkheadRegistry.getBulkhead(name)
    
    fun decorateSuspendFunction(
        circuitBreakerName: String,
        retryName: String,
        bulkheadName: String,
        suspendFunction: suspend () -> Unit
    ): suspend () -> Unit {
        val circuitBreaker = getCircuitBreaker(circuitBreakerName)
        val retry = getRetry(retryName)
        val bulkhead = getBulkhead(bulkheadName)
        
        return {
            bulkhead.executeTask {
                io.github.resilience4j.kotlin.retry.retry(retry) {
                    io.github.resilience4j.kotlin.circuitbreaker.executeSuspendFunction(circuitBreaker) {
                        suspendFunction()
                    }
                }
            }
        }
    }
}