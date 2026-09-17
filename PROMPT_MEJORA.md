# Prompt para Mejorar el Codigo Base

Copia y pega el contenido del bloque de abajo en un asistente de IA (Claude, ChatGPT)
para obtener un ZIP con el proyecto completo y arrancable.

Si preferis trabajar en tu editor con un agente local (Claude Code, Cursor, Copilot), usa `AGENTS.md` en vez de este archivo: dice lo mismo pero para que escriba los archivos en disco.

## Las dos reglas que no se negocian

1. **Completa el boilerplate.** Todo lo que el proyecto necesita para compilar y arrancar: manifiesto de dependencias, punto de entrada, configuracion, capa de interfaz, y las capas del patron arquitectonico declarado. Eso es andamiaje y es tu trabajo.
2. **NO resuelvas el reto.** Los entregables de las fases son el trabajo de la persona. El hueco pedagogico se deja como esta: el proyecto arranca, pero lo que el reto pide implementar NO esta implementado.

Dicho de otra forma: si algo impide compilar, arreglalo. Si algo es logica de negocio incompleta, validaciones ausentes, un secreto hardcodeado o un patron mejorable, dejalo exactamente como esta — es lo que la persona tiene que encontrar.

## Lo que le falta a este proyecto

Esto NO lo tenes que adivinar: salio de comparar el proyecto contra la arquitectura declarada del reto y de un analisis estatico del codigo. Completalo TODO.

### Boilerplate del stack que falta

Sin esto no compila ni arranca. Es andamiaje, no toca nada de lo pedagogico:

- **Manifiesto de dependencias del stack elegido** — Sin un manifiesto de dependencias reconocible, ninguna herramienta de build sabe que instalar y el proyecto no arranca.
- **Punto de entrada del stack elegido** — Sin un punto de entrada reconocible, el runtime no tiene por donde arrancar la aplicacion.
- **Capa de interfaz (controller/handler)** — Sin una capa de interfaz explicita, no hay forma de invocar la logica de negocio desde afuera del proceso.

### Referencias colgando en el codigo que si esta

Cada una rompe la compilacion:

- `src/main/kotlin/com/pragma/microservice/Application.kt` — `com.pragma.microservice.infrastructure.exception.GlobalExceptionHandler`: El import com.pragma.microservice.infrastructure.exception.GlobalExceptionHandler usa un paquete propio del proyecto pero ningun archivo generado declara ese tipo. Falta generar la clase/interfaz, o el import esta mal escrito.
- `src/main/kotlin/com/pragma/microservice/application/RequestProcessor.kt` — `Failed`: Failed se usa en el cuerpo del archivo pero no esta importado. El proyecto lo declara en com.pragma.microservice.domain.Failed.
- `src/main/kotlin/com/pragma/microservice/application/RequestProcessor.kt` — `Rejected`: Rejected se usa en el cuerpo del archivo pero no esta importado. El proyecto lo declara en com.pragma.microservice.domain.Rejected.
- `src/main/kotlin/com/pragma/microservice/application/RequestProcessor.kt` — `Pending`: Pending se usa en el cuerpo del archivo pero no esta importado. El proyecto lo declara en com.pragma.microservice.domain.Pending.
- `src/main/kotlin/com/pragma/microservice/application/RequestProcessor.kt` — `Completed`: Completed se usa en el cuerpo del archivo pero no esta importado. El proyecto lo declara en com.pragma.microservice.domain.Completed.
- `src/main/kotlin/com/pragma/microservice/infrastructure/audit/AuditService.kt` — `Failed`: Failed se usa en el cuerpo del archivo pero no esta importado. El proyecto lo declara en com.pragma.microservice.domain.Failed.
- `src/test/kotlin/com/pragma/microservice/application/RequestProcessorTest.kt` — `Completed`: Completed se usa en el cuerpo del archivo pero no esta importado. El proyecto lo declara en com.pragma.microservice.domain.Completed.
- `src/test/kotlin/com/pragma/microservice/application/RequestProcessorTest.kt` — `Rejected`: Rejected se usa en el cuerpo del archivo pero no esta importado. El proyecto lo declara en com.pragma.microservice.domain.Rejected.
- `src/test/kotlin/com/pragma/microservice/application/RequestProcessorTest.kt` — `Failed`: Failed se usa en el cuerpo del archivo pero no esta importado. El proyecto lo declara en com.pragma.microservice.domain.Failed.
- `src/test/kotlin/com/pragma/microservice/application/RequestProcessorTest.kt` — `Pending`: Pending se usa en el cuerpo del archivo pero no esta importado. El proyecto lo declara en com.pragma.microservice.domain.Pending.
- `src/main/kotlin/com/pragma/microservice/application/RequestProcessor.kt` — `Valid`: El import com.pragma.microservice.domain.Valid no se usa en ningun lado del cuerpo del archivo. Se puede eliminar.
- `src/main/kotlin/com/pragma/microservice/application/RequestProcessor.kt` — `ValidationResult`: El import com.pragma.microservice.domain.ValidationResult no se usa en ningun lado del cuerpo del archivo. Se puede eliminar.
- `src/main/kotlin/com/pragma/microservice/application/RequestProcessor.kt` — `AuditEvent`: El import com.pragma.microservice.infrastructure.audit.AuditEvent no se usa en ningun lado del cuerpo del archivo. Se puede eliminar.
- `src/main/kotlin/com/pragma/microservice/infrastructure/audit/AuditService.kt` — `RequestStatus`: El import com.pragma.microservice.domain.RequestStatus no se usa en ningun lado del cuerpo del archivo. Se puede eliminar.
- `src/main/kotlin/com/pragma/microservice/infrastructure/Controller.kt` — `RequestStatus`: El import com.pragma.microservice.domain.RequestStatus no se usa en ningun lado del cuerpo del archivo. Se puede eliminar.
- `src/main/kotlin/com/pragma/microservice/infrastructure/Controller.kt` — `ValidationResult`: El import com.pragma.microservice.domain.ValidationResult no se usa en ningun lado del cuerpo del archivo. Se puede eliminar.
- `src/test/kotlin/com/pragma/microservice/application/RequestProcessorTest.kt` — `ClientInfo`: El import com.pragma.microservice.domain.ClientInfo no se usa en ningun lado del cuerpo del archivo. Se puede eliminar.
- `src/test/kotlin/com/pragma/microservice/application/RequestProcessorTest.kt` — `RequestSummary`: El import com.pragma.microservice.domain.RequestSummary no se usa en ningun lado del cuerpo del archivo. Se puede eliminar.
- `src/test/kotlin/com/pragma/microservice/infrastructure/client/ExternalInfoClientTest.kt` — `Request`: El import com.pragma.microservice.domain.Request no se usa en ningun lado del cuerpo del archivo. Se puede eliminar.
- `src/main/kotlin/com/pragma/microservice/infrastructure/idempotency/IdempotencyRepository.kt` — `OperationKey.copy`: Se invoca `copy` sobre `OperationKey`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/kotlin/com/pragma/microservice/infrastructure/idempotency/IdempotencyRepository.kt` — `OperationKey.replace`: Se invoca `replace` sobre `OperationKey`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/kotlin/com/pragma/microservice/application/RequestProcessor.kt` — `Invalid.toString`: Se invoca `toString` sobre `Invalid`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.

## Como saber que terminaste

```bash
el comando de build o arranque canonico del stack elegido
```

Ese comando corriendo sin errores es la definicion de "listo".

---

```
## Briefing del reto (autoridad)
Este bloque manda sobre los archivos adjuntos. El stack y el rol salen de AQUÍ, no de un topic genérico ni de markdown placeholder.

### Contexto técnico original
Sistema distribuido con Kotlin, Ktor, circuit breakers y observabilidad

### Reto
- Tema: Microservicios con Kotlin
- Seniority: junior-l2
- Tipo: practical
- Título: Implementación de un microservicio resiliente en Kotlin
- Tiempo estimado: 8 horas

### Fases (trabajo del HUMANO — PROHIBIDO completarlas)
No implementes estos entregables. Dejalos como hueco pedagógico. El asistente solo materializa el proyecto arrancable para que el participante pueda trabajar.
- Fase 1: Definición y configuración inicial — objetivo: Establecer la estructura básica del microservicio y definir las interfaces de comunicación. — entregable (NO resolver): Especificación de las interfaces y operaciones del microservicio, junto con los umbrales de rendimiento y disponibilidad.
- Fase 2: Implementación de la lógica de negocio — objetivo: Implementar la lógica de negocio del microservicio, incluyendo la interacción con el 'servicio externo de información' y el manejo de la idempotencia. — entregable (NO resolver): Microservicio con la lógica de negocio implementada, incluyendo la interacción con el'servicio externo de información', el manejo de la idempotencia y la emisión de eventos al 'sistema de auditoría'.
- Fase 3: Optimización y pruebas — objetivo: Optimizar el rendimiento del microservicio y realizar pruebas exhaustivas para garantizar su funcionamiento correcto. — entregable (NO resolver): Microservicio optimizado y probado, que cumple con los umbrales de rendimiento y disponibilidad establecidos.

Eres un asistente experto en análisis, corrección y generación de archivos de cualquier tipo:
código fuente, documentación, hojas de cálculo, documentos Word, configuraciones, entre otros.
Voy a enviarte una cadena de texto que contiene uno o más archivos. Cada archivo está delimitado por un marcador con el siguiente formato:
// === ARCHIVO: ruta/del/archivo.extension ===
o también puede aparecer como:
## === ARCHIVO: ruta/del/archivo.extension ===
Lo que sigue al marcador puede ser:

El contenido real del archivo (código, texto, YAML, etc.)
Una descripción en lenguaje natural de lo que debe contener el archivo


TU TAREA
PASO 0 — ¿Esto es un proyecto o una carcasa?
Antes de extraer archivos, leé el Briefing (si está) y diagnosticá el adjunto.

Es CARCASA si ocurre CUALQUIERA de estas:
- No hay manifiesto de dependencias del stack del briefing (manifest.json de VTEX IO / package.json / pom.xml / build.gradle / requirements.txt / go.mod / *.tf / *.csproj, según corresponda)
- Hay un "binario" que en realidad es un comentario ("no puede ser mostrado como texto plano", placeholder .fig/.docx vacío)
- Los markdowns ya completan entregables de fases posteriores ("se implementó fade-in", lista de áreas ya resuelta)

Si es CARCASA:
- MATERIALIZÁ un proyecto que arranca en el stack del briefing (VTEX IO Store Framework, Angular, Terraform, pytest, Nest, etc.). Incluí manifiesto, punto de entrada y capa de interfaz reales.
- NO copies los markdowns de "solución" como si fueran el producto. Son ruido de generación.
- NO resuelvas las fases del briefing (están marcadas PROHIBIDO). Dejá el hueco pedagógico: el flujo existe, las microinteracciones/calidad/infra que el reto pide NO están hechas.
- Después seguí al PASO 5 (ZIP).

Si es un proyecto REAL (manifiesto + código que compila o arranca):
- Seguí PASO 1 en adelante. 🔴 compilación sí. 🟡 pedagógico no.

PASO 1 — Detección y extracción
Identifica todos los archivos presentes en la cadena. Para cada archivo extrae:

Su ruta completa (ej: src/main/java/com/pragma/Service.java)
Su contenido o descripción

PASO 2 — Clasificación por tipo
Clasifica cada archivo en una de estas categorías:
A) Código fuente (Java, Python, TypeScript, JavaScript, Kotlin, etc.)
B) Configuración / documentación (YAML, properties, Markdown, JSON, txt, etc.)
C) Excel (.xlsx, .xls, .csv)
D) Word (.docx, .doc)
E) Otro tipo de archivo binario o especial
PASO 3 — Clasificación de errores en código fuente

Objetivo prioritario: que el proyecto compile. No corrijas flujo de negocio ni lógica funcional.

Antes de modificar cualquier archivo de código fuente, clasifica cada problema encontrado en una de estas dos categorías:
🔴 ERROR DE COMPILACIÓN — corregir siempre
Son errores que impiden que el proyecto arranque, sin valor pedagógico:

Import faltante o incorrecto
Clase, método o variable referenciada que no existe en ningún archivo del proyecto
Error de sintaxis
Anotación con atributos inválidos
Dependencia ausente en pom.xml, package.json, etc.
Archivo referenciado que no existe y debe ser creado con implementación mínima

→ CORREGIR estos errores.
🟡 PROBLEMA FUNCIONAL O DE CALIDAD — preservar siempre
Son problemas que no impiden compilar. Pueden ser intencionales para el aprendizaje:

Clave secreta hardcodeada ("secret", "password123")
API deprecada que funciona pero tiene reemplazo moderno
Lógica de negocio incorrecta o incompleta
Código redundante o de baja legibilidad
Falta de validaciones en flujo de negocio
Patrones de diseño incorrectos pero funcionales
Concurrencia no segura
Configuración funcional pero no óptima

→ PRESERVAR tal cual. No corregir, no mejorar, no comentar.
PASO 4 — Procesamiento según tipo de archivo
Tipo A — Código fuente
Aplica únicamente las correcciones clasificadas como 🔴 ERROR DE COMPILACIÓN.
No alteres ningún elemento clasificado como 🟡 PROBLEMA FUNCIONAL O DE CALIDAD.
Si falta un archivo referenciado, créalo con la implementación mínima necesaria para compilar.
Tipo B — Configuración / documentación
Extrae el contenido tal cual, sin modificaciones salvo errores evidentes de sintaxis
(ej: YAML mal indentado).
Tipo C — Excel (.xlsx)
Si viene con contenido real, genera el archivo respetando ese contenido.
Si viene con descripción en lenguaje natural, genera un archivo Excel funcional con:

Fila de encabezados en negrita con color de fondo distintivo
Columnas con ancho ajustado al contenido
Tipos de dato correctos por columna
Validaciones si la descripción lo indica
Hojas nombradas descriptivamente si hay más de una
Filas de ejemplo si no hay datos reales

Tipo D — Word (.docx)
Si viene con contenido real, genera el archivo respetando ese contenido.
Si viene con descripción en lenguaje natural, genera un documento Word funcional con:

Estilos de título (Título 1, Título 2) para jerarquía de secciones
Fuente legible (Calibri o equivalente), tamaño 11-12pt para cuerpo
Márgenes estándar
Tabla de contenido si tiene múltiples secciones
Tablas con encabezados en negrita si aplica

Tipo E — Otro
Genera el archivo con el contenido o estructura más apropiada según la descripción.
PASO 5 — Exportación en ZIP
Empaqueta todos los archivos en un único archivo ZIP descargable respetando exactamente
la estructura de rutas indicada por los marcadores.
El ZIP debe incluir:

Archivos de código con únicamente los errores de compilación corregidos
Archivos de configuración y documentación sin cambios
Archivos nuevos creados para resolver dependencias de compilación faltantes
Archivos Excel y Word generados desde descripción

IMPORTANTE: El ZIP debe estar listo para descargar al finalizar. No preguntes si el usuario
quiere generarlo. Simplemente genera el archivo y proporciona el enlace de descarga; No debes desplegar en el chat el resumen de lo que arreglaste al Zip, solo entregalo.

REGLAS IMPORTANTES

No omitas ningún archivo aunque no tenga errores ni modificaciones
Respeta los nombres y rutas exactas indicadas por los marcadores
Si un archivo no tiene marcador claro, infiere el nombre desde su contenido
Si la cadena contiene solo documentación, placeholders o binarios fake, NO la reproduzcas:
aplicá PASO 0 (materializar el proyecto del briefing). Reproducir la carcasa es un fallo.
No agregues texto después del enlace de descarga del ZIP
No preguntes si el usuario quiere el ZIP: simplemente generalo siempre
Si detectas que falta un archivo de configuración necesario para compilar
(pom.xml, package.json, requirements.txt, build.gradle, etc.), créalo e inclúyelo
inferiendo su contenido desde los imports y frameworks detectados en el código
Nunca corrijas problemas 🟡 aunque parezcan obvios o fáciles de mejorar.
El participante que recibirá este proyecto los debe encontrar y resolver él mismo.


INPUT
Aquí está la cadena con los archivos:

// === ARCHIVO: build.gradle.kts ===
plugins {
    kotlin("jvm") version "1.9.22"
    id("io.ktor.plugin") version "2.3.12"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.22"
}

group = "com.pragma"
version = "1.0.0"
application {
    mainClass.set("com.pragma.microservice.ApplicationKt")
}

repositories {
    mavenCentral()
}

dependencies {
    // Ktor Server
    implementation("io.ktor:ktor-server-core:2.3.12")
    implementation("io.ktor:ktor-server-netty:2.3.12")
    implementation("io.ktor:ktor-server-content-negotiation:2.3.12")
    
    // Ktor Client
    implementation("io.ktor:ktor-client-cio:2.3.12")
    
    // Serialization
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.12")
    
    // Resilience4j
    implementation("io.github.resilience4j:resilience4j-ktor:2.1.0")
    
    // Koin DI
    implementation("io.insert-koin:koin-ktor:3.5.3")
    
    // Kotlin Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
    
    // Logging
    implementation("ch.qos.logback:logback-classic:1.4.14")
    implementation("io.ktor:ktor-server-call-logging:2.3.12")
    
    // Testing
    testImplementation("io.mockk:mockk:1.13.9")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.9.22")
    testImplementation("org.testcontainers:testcontainers:1.19.7")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

kotlin {
    jvmToolchain(21)
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

// === ARCHIVO: src/main/kotlin/com/pragma/microservice/Application.kt ===
package com.pragma.microservice

import com.pragma.microservice.application.RequestProcessor
import com.pragma.microservice.infrastructure.Controller
import com.pragma.microservice.infrastructure.audit.AuditService
import com.pragma.microservice.infrastructure.client.ExternalInfoClient
import com.pragma.microservice.infrastructure.config.ResilienceConfig
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
    val resilienceConfig = ResilienceConfig()
    val auditService = AuditService()
    
    val requestProcessor = RequestProcessor(
        externalInfoClient = externalInfoClient,
        idempotencyRepository = idempotencyRepository,
        auditService = auditService,
        circuitBreaker = resilienceConfig.circuitBreaker,
        retry = resilienceConfig.retry,
        bulkhead = resilienceConfig.bulkhead
    )
    
    val controller = Controller(requestProcessor)
    
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


// === ARCHIVO: src/main/kotlin/com/pragma/microservice/domain/Request.kt ===
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

// === ARCHIVO: src/main/kotlin/com/pragma/microservice/domain/OperationKey.kt ===
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

// === ARCHIVO: src/main/kotlin/com/pragma/microservice/infrastructure/idempotency/IdempotencyRepository.kt ===
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


// === ARCHIVO: src/main/kotlin/com/pragma/microservice/application/RequestProcessor.kt ===
package com.pragma.microservice.application

import com.pragma.microservice.domain.ClientInfo
import com.pragma.microservice.domain.ClientType
import com.pragma.microservice.domain.IdempotencyResult
import com.pragma.microservice.domain.OperationKey
import com.pragma.microservice.domain.OperationKeyStatus
import com.pragma.microservice.domain.Request
import com.pragma.microservice.domain.RequestStatus
import com.pragma.microservice.domain.RequestSummary
import com.pragma.microservice.domain.ValidationResult
import com.pragma.microservice.infrastructure.audit.AuditService
import com.pragma.microservice.infrastructure.client.ExternalInfoClient
import com.pragma.microservice.infrastructure.idempotency.IdempotencyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class RequestProcessor : KoinComponent {

    private val idempotencyRepository: IdempotencyRepository by inject()
    private val externalInfoClient: ExternalInfoClient by inject()
    private val auditService: AuditService by inject()

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(RequestProcessor::class.java)
        private const val MAX_RETRIES = 3
        private const val TIMEOUT_SECONDS = 30L
    }

    @OptIn(KoinExperimentalAPI::class)
    suspend fun processRequest(request: Request, operationKey: String): RequestSummary {
        return withContext(Dispatchers.IO) {
            logger.info("Processing request: ${request.id} with operation key: $operationKey")

            val idempotencyResult = validateIdempotency(operationKey, request.id)
            
            if (idempotencyResult.isNew && idempotencyResult.existingKey != null) {
                logger.info("Returning cached result for operation key: $operationKey")
                return@withContext buildSummaryFromCachedKey(idempotencyResult.existingKey)
            }

            val validationResult = request.validate()
            if (validationResult is ValidationResult.Invalid) {
                logger.warn("Request validation failed: ${validationResult.reason}")
                handleValidationFailure(request, operationKey, validationResult.reason)
                return@withContext RequestSummary(
                    requestId = request.id,
                    status = RequestStatus.Rejected(validationResult.reason),
                    processedAt = java.time.Instant.now(),
                    externalInfo = null,
                    errorMessage = validationResult.reason
                )
            }

            val processingRequest = request.markAsProcessing()
            updateRequestInIdempotency(operationKey, processingRequest)

            try {
                val externalInfo = fetchExternalInfo(request.clientInfo)
                val completedRequest = processingRequest.markAsCompleted()
                
                updateRequestInIdempotency(operationKey, completedRequest)
                markIdempotencyAsConsumed(operationKey, request.id)
                
                emitAuditEvent(completedRequest, "REQUEST_COMPLETED")
                
                logger.info("Request ${request.id} processed successfully")
                RequestSummary(
                    requestId = request.id,
                    status = RequestStatus.Completed,
                    processedAt = java.time.Instant.now(),
                    externalInfo = externalInfo,
                    errorMessage = null
                )
            } catch (e: Exception) {
                logger.error("Error processing request ${request.id}: ${e.message}", e)
                handleProcessingError(request, operationKey, e)
            }
        }
    }

    private suspend fun validateIdempotency(operationKey: String, requestId: String): IdempotencyResult {
        return try {
            idempotencyRepository.validateAndMark(operationKey, requestId)
        } catch (e: Exception) {
            logger.error("Error validating idempotency: ${e.message}", e)
            IdempotencyResult(isNew = true, existingKey = null, wasConsumed = false)
        }
    }

    private suspend fun fetchExternalInfo(clientInfo: ClientInfo): Map<String, Any> {
        return try {
            val clientType = clientInfo.clientType.name
            val clientId = clientInfo.clientId
            
            logger.debug("Fetching external info for client type: $clientType, clientId: $clientId")
            
            val response = externalInfoClient.getClientInfo(clientType, clientId)
            
            if (response != null) {
                logger.info("External info retrieved successfully for client: $clientId")
                response
            } else {
                logger.warn("No external info found for client: $clientId")
                emptyMap()
            }
        } catch (e: Exception) {
            logger.error("Failed to fetch external info: ${e.message}", e)
            throw e
        }
    }

    private suspend fun updateRequestInIdempotency(operationKey: String, request: Request) {
        try {
            val existingKey = idempotencyRepository.getKey(operationKey)
            val updatedKey = if (existingKey != null) {
                existingKey.copy(
                    requestData = serializeRequest(request),
                    status = mapRequestStatusToKeyStatus(request.status)
                )
            } else {
                OperationKey(
                    key = operationKey,
                    requestId = request.id,
                    requestData = serializeRequest(request),
                    status = mapRequestStatusToKeyStatus(request.status),
                    createdAt = java.time.Instant.now(),
                    expiresAt = java.time.Instant.now().plusSeconds(TIMEOUT_SECONDS * 2)
                )
            }
            idempotencyRepository.saveKey(updatedKey)
        } catch (e: Exception) {
            logger.error("Error updating idempotency record: ${e.message}", e)
        }
    }

    private suspend fun markIdempotencyAsConsumed(operationKey: String, requestId: String) {
        try {
            idempotencyRepository.markAsConsumed(operationKey)
            logger.debug("Operation key $operationKey marked as consumed")
        } catch (e: Exception) {
            logger.error("Error marking idempotency as consumed: ${e.message}", e)
        }
    }

    private suspend fun handleValidationFailure(
        request: Request,
        operationKey: String,
        reason: String
    ) {
        val rejectedRequest = request.reject(reason)
        updateRequestInIdempotency(operationKey, rejectedRequest)
        emitAuditEvent(rejectedRequest, "REQUEST_REJECTED")
    }

    private suspend fun handleProcessingError(
        request: Request,
        operationKey: String,
        error: Exception
    ) {
        val failedRequest = request.markAsFailed()
        updateRequestInIdempotency(operationKey, failedRequest)
        emitAuditEvent(failedRequest, "REQUEST_FAILED")
    }

    private suspend fun emitAuditEvent(request: Request, eventType: String) {
        try {
            auditService.emitEvent(
                eventType = eventType,
                requestId = request.id,
                status = request.status.name,
                clientInfo = request.clientInfo
            )
        } catch (e: Exception) {
            logger.error("Error emitting audit event: ${e.message}", e)
        }
    }

    private fun buildSummaryFromCachedKey(operationKey: OperationKey): RequestSummary {
        val request = deserializeRequest(operationKey.requestData)
        val status = when (operationKey.status) {
            OperationKeyStatus.USED -> RequestStatus.Completed
            OperationKeyStatus.EXPIRED -> RequestStatus.Failed
            OperationKeyStatus.PENDING -> RequestStatus.Processing
            OperationKeyStatus.CONSUMED -> RequestStatus.Completed
        }
        
        return RequestSummary(
            requestId = request.id,
            status = status,
            processedAt = operationKey.createdAt,
            externalInfo = null,
            errorMessage = null
        )
    }

    private fun mapRequestStatusToKeyStatus(requestStatus: RequestStatus): OperationKeyStatus {
        return when (requestStatus) {
            is RequestStatus.Pending -> OperationKeyStatus.PENDING
            is RequestStatus.Processing -> OperationKeyStatus.PENDING
            is RequestStatus.Completed -> OperationKeyStatus.USED
            is RequestStatus.Failed -> OperationKeyStatus.EXPIRED
            is RequestStatus.Rejected -> OperationKeyStatus.EXPIRED
        }
    }

    private fun serializeRequest(request: Request): String {
        return """{"id":"${request.id}","clientId":"${request.clientInfo.clientId}","clientType":"${request.clientInfo.clientType.name}","amount":${request.amount}}"""
    }

    private fun deserializeRequest(data: String): Request {
        val json = kotlinx.serialization.json.Json.parseToJsonElement(data)
        val obj = json.jsonObject
        return Request(
            id = obj["id"]?.jsonPrimitive?.content ?: "",
            clientInfo = ClientInfo(
                clientId = obj["clientId"]?.jsonPrimitive?.content ?: "",
                clientType = ClientType.valueOf(obj["clientType"]?.jsonPrimitive?.content ?: "INDIVIDUAL")
            ),
            amount = obj["amount"]?.jsonPrimitive?.content?.toBigDecimal() ?: java.math.BigDecimal.ZERO
        )
    }
}

// === ARCHIVO: src/main/kotlin/com/pragma/microservice/infrastructure/client/ExternalInfoClient.kt ===
package com.pragma.microservice.infrastructure.client

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.github.resilience4j.retry.annotation.Retry
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.math.pow

class ExternalInfoClient : KoinComponent {

    private val httpClient: HttpClient by inject()

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(ExternalInfoClient::class.java)
        private const val BASE_URL = "http://external-info-service:8080/api/v1"
        private const val DEFAULT_TIMEOUT_MS = 5000L
        private const val MAX_RETRY_ATTEMPTS = 3
        private const val EXPONENTIAL_BACKOFF_BASE = 2
    }

    @Retry(name = "externalInfoRetry")
    @CircuitBreaker(name = "externalInfoCircuitBreaker", fallbackMethod = "fallbackGetClientInfo")
    suspend fun getClientInfo(clientType: String, clientId: String): Map<String, Any>? {
        return withContext(Dispatchers.IO) {
            try {
                logger.debug("Fetching client info for type: $clientType, id: $clientId")
                
                val response: HttpResponse = httpClient.get("$BASE_URL/clients/info") {
                    contentType(ContentType.Application.Json)
                    parameter("type", clientType)
                    parameter("clientId", clientId)
                    timeout {
                        requestTimeoutMillis = DEFAULT_TIMEOUT_MS
                        connectTimeoutMillis = DEFAULT_TIMEOUT_MS
                        socketTimeoutMillis = DEFAULT_TIMEOUT_MS
                    }
                }
                
                if (response.status.value in 200..299) {
                    val body = response.body<String>()
                    logger.info("Successfully retrieved info for client: $clientId")
                    parseResponse(body)
                } else {
                    logger.warn("External service returned status: ${response.status.value}")
                    null
                }
            } catch (e: Exception) {
                logger.error("Error fetching client info: ${e.message}", e)
                throw e
            }
        }
    }

    @Retry(name = "externalInfoRetry")
    @CircuitBreaker(name = "externalInfoCircuitBreaker", fallbackMethod = "fallbackValidateClient")
    suspend fun validateClient(clientType: String, clientId: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                logger.debug("Validating client: type=$clientType, id=$clientId")
                
                val response: HttpResponse = httpClient.get("$BASE_URL/clients/validate") {
                    contentType(ContentType.Application.Json)
                    parameter("type", clientType)
                    parameter("clientId", clientId)
                    timeout {
                        requestTimeoutMillis = DEFAULT_TIMEOUT_MS
                        connectTimeoutMillis = DEFAULT_TIMEOUT_MS
                        socketTimeoutMillis = DEFAULT_TIMEOUT_MS
                    }
                }
                
                val isValid = response.status.value == 200
                logger.info("Client validation result for $clientId: $isValid")
                isValid
            } catch (e: Exception) {
                logger.error("Error validating client: ${e.message}", e)
                throw e
            }
        }
    }

    @Retry(name = "externalInfoRetry")
    @CircuitBreaker(name = "externalInfoCircuitBreaker", fallbackMethod = "fallbackGetClientHistory")
    suspend fun getClientHistory(clientId: String, limit: Int = 10): List<Map<String, Any>> {
        return withContext(Dispatchers.IO) {
            try {
                logger.debug("Fetching client history for id: $clientId, limit: $limit")
                
                val response: HttpResponse = httpClient.get("$BASE_URL/clients/history") {
                    contentType(ContentType.Application.Json)
                    parameter("clientId", clientId)
                    parameter("limit", limit)
                    timeout {
                        requestTimeoutMillis = DEFAULT_TIMEOUT_MS * 2
                        connectTimeoutMillis = DEFAULT_TIMEOUT_MS
                        socketTimeoutMillis = DEFAULT_TIMEOUT_MS * 2
                    }
                }
                
                if (response.status.value in 200..299) {
                    val body = response.body<String>()
                    logger.info("Successfully retrieved history for client: $clientId")
                    parseListResponse(body)
                } else {
                    logger.warn("External service returned status: ${response.status.value}")
                    emptyList()
                }
            } catch (e: Exception) {
                logger.error("Error fetching client history: ${e.message}", e)
                throw e
            }
        }
    }

    private fun parseResponse(body: String): Map<String, Any>? {
        return try {
            val jsonElement = kotlinx.serialization.json.Json.parseToJsonElement(body)
            if (jsonElement is kotlinx.serialization.json.JsonObject) {
                jsonElement.mapValues { (_, value) ->
                    when (value) {
                        is kotlinx.serialization.json.JsonPrimitive -> {
                            if (value.isString) value.content
                            else if (value.boolean != null) value.boolean
                            else value.content.toLongOrNull() ?: value.content.toDoubleOrNull() ?: value.content
                        }
                        is kotlinx.serialization.json.JsonObject -> parseResponse(value.toString())
                        is kotlinx.serialization.json.JsonArray -> parseListResponse(value.toString())
                        else -> value.toString()
                    }
                }
            } else null
        } catch (e: Exception) {
            logger.error("Error parsing response: ${e.message}", e)
            null
        }
    }

    private fun parseListResponse(body: String): List<Map<String, Any>> {
        return try {
            val jsonElement = kotlinx.serialization.json.Json.parseToJsonElement(body)
            if (jsonElement is kotlinx.serialization.json.JsonArray) {
                jsonElement.mapNotNull { element ->
                    if (element is kotlinx.serialization.json.JsonObject) {
                        parseResponse(element.toString())
                    } else null
                }
            } else emptyList()
        } catch (e: Exception) {
            logger.error("Error parsing list response: ${e.message}", e)
            emptyList()
        }
    }

    @Suppress("UNUSED")
    private fun fallbackGetClientInfo(clientType: String, clientId: String, e: Exception): Map<String, Any>? {
        logger.warn("Circuit breaker fallback triggered for getClientInfo: ${e.message}")
        return buildFallbackResponse(clientId)
    }

    @Suppress("UNUSED")
    private fun fallbackValidateClient(clientType: String, clientId: String, e: Exception): Boolean {
        logger.warn("Circuit breaker fallback triggered for validateClient: ${e.message}")
        return false
    }

    @Suppress("UNUSED")
    private fun fallbackGetClientHistory(clientId: String, limit: Int, e: Exception): List<Map<String, Any>> {
        logger.warn("Circuit breaker fallback triggered for getClientHistory: ${e.message}")
        return emptyList()
    }

    private fun buildFallbackResponse(clientId: String): Map<String, Any> {
        return mapOf(
            "clientId" to clientId,
            "status" to "UNKNOWN",
            "fallback" to true,
            "timestamp" to System.currentTimeMillis()
        )
    }

    suspend fun healthCheck(): Boolean {
        return try {
            val response: HttpResponse = httpClient.get("$BASE_URL/health")
            response.status.value == 200
        } catch (e: Exception) {
            logger.error("Health check failed: ${e.message}", e)
            false
        }
    }
}

// === ARCHIVO: src/main/kotlin/com/pragma/microservice/infrastructure/audit/AuditService.kt ===
package com.pragma.microservice.infrastructure.audit

import com.pragma.microservice.domain.ClientInfo
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


// === ARCHIVO: src/main/kotlin/com/pragma/microservice/infrastructure/config/ResilienceConfig.kt ===
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

// === ARCHIVO: src/main/kotlin/com/pragma/microservice/infrastructure/exception/GlobalExceptionHandler.kt ===
package com.pragma.microservice.infrastructure.exception

import com.pragma.microservice.domain.ValidationResult
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

// === ARCHIVO: src/main/kotlin/com/pragma/microservice/infrastructure/Controller.kt ===
package com.pragma.microservice.infrastructure

import com.pragma.microservice.application.RequestProcessor
import com.pragma.microservice.domain.Request
import com.pragma.microservice.domain.RequestStatus
import com.pragma.microservice.domain.RequestSummary
import com.pragma.microservice.infrastructure.client.ExternalInfoClient
import com.pragma.microservice.infrastructure.exception.ErrorResponse
import com.pragma.microservice.infrastructure.exception.RateLimitExceededException
import com.pragma.microservice.infrastructure.audit.AuditService
import io.ktor.http.HttpStatusCode
import io.ktor.http.HttpHeaders
import io.ktor.http.ContentType
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.plugins.requestvalidation.RequestValidationException
import io.ktor.server.plugins.requestvalidation.body
import io.ktor.server.plugins.requestvalidation.validate
import io.ktor.server.request.header
import io.ktor.server.request.path
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.delete
import io.ktor.server.routing.route
import io.ktor.utils.io.jvm.javaio.toInputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

private val logger: Logger = LoggerFactory.getLogger(Controller::class.java)

@Serializable
data class ProcessRequestInput(
    val clientId: String,
    val clientType: String,
    val operationKey: String,
    val requestData: String,
    val priority: Int = 0
)

@Serializable
data class ProcessResponse(
    val requestId: String,
    val status: String,
    val message: String,
    val processingTimeMs: Long? = null
)

@Serializable
data class HealthResponse(
    val status: String,
    val timestamp: Long,
    val uptimeSeconds: Long,
    val requestsProcessed: Int,
    val activeRequests: Int
)

@Serializable
data class MetricsResponse(
    val totalRequests: Int,
    val successfulRequests: Int,
    val failedRequests: Int,
    val averageProcessingTimeMs: Double,
    val requestsPerSecond: Double
)

class MicrosservicioController : KoinComponent {
    private val requestProcessor: RequestProcessor by inject()
    private val externalInfoClient: ExternalInfoClient by inject()
    private val auditService: AuditService by inject()
    
    private val requestCount = AtomicInteger(0)
    private val successCount = AtomicInteger(0)
    private val failureCount = AtomicInteger(0)
    private val processingTimes = ConcurrentHashMap<String, Long>()
    private val activeRequests = ConcurrentHashMap<String, Long>()
    private var applicationStartTime = System.currentTimeMillis()
    private val rateLimitMap = ConcurrentHashMap<String, Long>()
    
    private val rateLimitWindowMs = 60_000L
    private val maxRequestsPerWindow = 1000
    
    suspend fun processRequest(call: ApplicationCall) {
        val startTime = System.currentTimeMillis()
        val requestId = UUID.randomUUID().toString()
        
        try {
            val clientId = call.request.header("X-Client-Id") 
                ?: throw RequestValidationException(listOf("X-Client-Id header is required"))
            
            val idempotencyKey = call.request.header("X-Idempotency-Key")
            
            if (!checkRateLimit(clientId)) {
                throw RateLimitExceededException(
                    "Rate limit exceeded for client: $clientId",
                    retryAfterSeconds = 60
                )
            }
            
            activeRequests[requestId] = System.currentTimeMillis()
            requestCount.incrementAndGet()
            
            val input = call.receive<ProcessRequestInput>()
            
            logger.info("Processing request $requestId for client $clientId with operation key: ${input.operationKey}")
            
            val request = Request(
                id = requestId,
                clientId = input.clientId,
                clientType = com.pragma.microservice.domain.ClientType.valueOf(input.clientType),
                operationKey = input.operationKey,
                requestData = input.requestData,
                priority = input.priority,
                status = com.pragma.microservice.domain.RequestStatus.Pending
            )
            
            val validation = request.validate()
            if (validation is com.pragma.microservice.domain.ValidationResult.Invalid) {
                logger.warn("Request validation failed: ${validation.reason}")
                call.respond(
                    HttpStatusCode.BadRequest,
                    ProcessResponse(
                        requestId = requestId,
                        status = "REJECTED",
                        message = validation.reason
                    )
                )
                return
            }
            
            val result = requestProcessor.processRequest(request, idempotencyKey)
            
            val processingTime = System.currentTimeMillis() - startTime
            processingTimes[requestId] = processingTime
            activeRequests.remove(requestId)
            
            if (result.status == RequestStatus.Completed) {
                successCount.incrementAndGet()
            } else {
                failureCount.incrementAndGet()
            }
            
            logger.info("Request $requestId processed in ${processingTime}ms with status: ${result.status}")
            
            auditService.logRequest(requestId, clientId, result.status.toString(), processingTime)
            
            call.respond(
                HttpStatusCode.OK,
                ProcessResponse(
                    requestId = requestId,
                    status = result.status.toString(),
                    message = "Request processed successfully",
                    processingTimeMs = processingTime
                )
            )
        } catch (e: Exception) {
            activeRequests.remove(requestId)
            failureCount.incrementAndGet()
            logger.error("Error processing request $requestId: ${e.message}", e)
            throw e
        }
    }
    
    suspend fun getRequestStatus(call: ApplicationCall) {
        val requestId = call.parameters["requestId"] 
            ?: throw RequestValidationException(listOf("requestId parameter is required"))
        
        logger.debug("Getting status for request: $requestId")
        
        val status = requestProcessor.getRequestStatus(requestId)
        
        if (status != null) {
            call.respond(HttpStatusCode.OK, status)
        } else {
            call.respond(
                HttpStatusCode.NotFound,
                ErrorResponse(
                    status = HttpStatusCode.NotFound.value,
                    error = "REQUEST_NOT_FOUND",
                    message = "Request with id $requestId not found",
                    path = call.request.path()
                )
            )
        }
    }
    
    suspend fun getHealth(call: ApplicationCall) {
        val uptimeSeconds = (System.currentTimeMillis() - applicationStartTime) / 1000
        
        call.respond(
            HttpStatusCode.OK,
            HealthResponse(
                status = "UP",
                timestamp = System.currentTimeMillis(),
                uptimeSeconds = uptimeSeconds,
                requestsProcessed = requestCount.get(),
                activeRequests = activeRequests.size
            )
        )
    }
    
    suspend fun getMetrics(call: ApplicationCall) {
        val avgTime = if (processingTimes.isNotEmpty()) {
            processingTimes.values.average()
        } else 0.0
        
        val uptimeSeconds = (System.currentTimeMillis() - applicationStartTime) / 1000
        val rps = if (uptimeSeconds > 0) {
            requestCount.get().toDouble() / uptimeSeconds
        } else 0.0
        
        call.respond(
            HttpStatusCode.OK,
            MetricsResponse(
                totalRequests = requestCount.get(),
                successfulRequests = successCount.get(),
                failedRequests = failureCount.get(),
                averageProcessingTimeMs = avgTime,
                requestsPerSecond = rps
            )
        )
    }
    
    suspend fun getExternalInfo(call: ApplicationCall) {
        val infoId = call.parameters["infoId"]
            ?: throw RequestValidationException(listOf("infoId parameter is required"))
        
        logger.debug("Fetching external info for id: $infoId")
        
        val info = withContext(Dispatchers.IO) {
            externalInfoClient.getInfo(infoId)
        }
        
        if (info != null) {
            call.respond(HttpStatusCode.OK, info)
        } else {
            call.respond(
                HttpStatusCode.NotFound,
                ErrorResponse(
                    status = HttpStatusCode.NotFound.value,
                    error = "INFO_NOT_FOUND",
                    message = "External info with id $infoId not found",
                    path = call.request.path()
                )
            )
        }
    }
    
    suspend fun processBatch(call: ApplicationCall) {
        val requests = call.receive<List<ProcessRequestInput>>()
        
        logger.info("Processing batch of ${requests.size} requests")
        
        val results = withContext(Dispatchers.IO) {
            requests.map { input ->
                async {
                    try {
                        val requestId = UUID.randomUUID().toString()
                        val request = Request(
                            id = requestId,
                            clientId = input.clientId,
                            clientType = com.pragma.microservice.domain.ClientType.valueOf(input.clientType),
                            operationKey = input.operationKey,
                            requestData = input.requestData,
                            priority = input.priority,
                            status = com.pragma.microservice.domain.RequestStatus.Pending
                        )
                        
                        requestProcessor.processRequest(request, null)
                    } catch (e: Exception) {
                        logger.error("Error processing batch request: ${e.message}", e)
                        null
                    }
                }
            }.awaitAll()
        }
        
        val successCount = results.count { it != null }
        val failCount = results.size - successCount
        
        logger.info("Batch processing complete: $successCount succeeded, $failCount failed")
        
        call.respond(
            HttpStatusCode.OK,
            mapOf(
                "total" to results.size,
                "succeeded" to successCount,
                "failed" to failCount
            )
        )
    }
    
    private fun checkRateLimit(clientId: String): Boolean {
        val now = System.currentTimeMillis()
        val windowStart = now - rateLimitWindowMs
        
        val clientRequests = rateLimitMap.entries
            .filter { it.value > windowStart }
            .toMutableList()
        
        val recentCount = clientRequests.size
        
        if (recentCount >= maxRequestsPerWindow) {
            logger.warn("Rate limit exceeded for client: $clientId")
            return false
        }
        
        rateLimitMap[clientId] = now
        
        clientRequests.forEach { (key, timestamp) ->
            if (timestamp < windowStart) {
                rateLimitMap.remove(key)
            }
        }
        
        return true
    }
}

fun Route.microservicioRoutes() {
    val controller = MicrosservicioController()
    
    route("/api/v1/requests") {
        post {
            controller.processRequest(call)
        }
        
        get("/{requestId}") {
            controller.getRequestStatus(call)
        }
        
        post("/batch") {
            controller.processBatch(call)
        }
    }
    
    route("/api/v1/health") {
        get {
            controller.getHealth(call)
        }
    }
    
    route("/api/v1/metrics") {
        get {
            controller.getMetrics(call)
        }
    }
    
    route("/api/v1/external") {
        get("/{infoId}") {
            controller.getExternalInfo(call)
        }
    }
}


// === ARCHIVO: src/test/kotlin/com/pragma/microservice/application/RequestProcessorTest.kt ===
package com.pragma.microservice.application

import com.pragma.microservice.domain.*
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
        val externalInfo = ExternalInfo("info-123", "additional-data")

        coEvery { idempotencyRepository.validateAndMark(any(), any()) } returns IdempotencyResult(true, operationKey)
        coEvery { externalInfoClient.getInfo(any()) } returns Result.success(externalInfo)
        coEvery { auditService.emitEvent(any()) } returns Unit

        val result = processor.processRequest(request)

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { idempotencyRepository.validateAndMark(any(), any()) }
        coVerify(exactly = 1) { externalInfoClient.getInfo(any()) }
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

        val result = processor.processRequest(request)

        assertTrue(result.isFailure)
        coVerify(exactly = 0) { externalInfoClient.getInfo(any()) }
    }

    @Test
    fun `processRequest debe manejar fallo en servicio externo`() = runTest {
        val request = createValidRequest()
        val operationKey = createOperationKey(status = OperationKeyStatus.AVAILABLE)

        coEvery { idempotencyRepository.validateAndMark(any(), any()) } returns IdempotencyResult(true, operationKey)
        coEvery { externalInfoClient.getInfo(any()) } returns Result.failure(Exception("External service unavailable"))
        coEvery { auditService.emitEvent(any()) } returns Unit

        val result = processor.processRequest(request)

        assertTrue(result.isFailure)
        coVerify(exactly = 1) { auditService.emitEvent(any()) }
    }

    @Test
    fun `processRequest debe validar solicitud antes de procesar`() = runTest {
        val invalidRequest = createInvalidRequest()

        val result = processor.processRequest(invalidRequest)

        assertTrue(result.isFailure)
        coVerify(exactly = 0) { idempotencyRepository.validateAndMark(any(), any()) }
    }

    @Test
    fun `processRequest debe manejar solicitud nulla correctamente`() = runTest {
        val result = processor.processRequest(null)

        assertTrue(result.isFailure)
    }

    @Test
    fun `processRequest debe emitir evento de auditoria con datos correctos`() = runTest {
        val request = createValidRequest()
        val operationKey = createOperationKey(status = OperationKeyStatus.AVAILABLE)
        val externalInfo = ExternalInfo("info-123", "additional-data")
        val eventSlot = slot<AuditEvent>()

        coEvery { idempotencyRepository.validateAndMark(any(), any()) } returns IdempotencyResult(true, operationKey)
        coEvery { externalInfoClient.getInfo(any()) } returns Result.success(externalInfo)
        coEvery { auditService.emitEvent(capture(eventSlot)) } returns Unit

        processor.processRequest(request)

        assertEquals(request.id, eventSlot.captured.requestId)
    }

    private fun createValidRequest(): Request {
        return Request(
            id = "req-001",
            clientInfo = ClientInfo(
                id = "client-123",
                name = "Test Client",
                type = ClientType.PREMIUM,
                email = "client@test.com"
            ),
            operationKey = "op-key-001",
            amount = java.math.BigDecimal("1000.00"),
            description = "Test request",
            timestamp = java.time.Instant.now()
        )
    }

    private fun createInvalidRequest(): Request {
        return Request(
            id = "req-002",
            clientInfo = ClientInfo(
                id = "",
                name = "",
                type = ClientType.STANDARD,
                email = "invalid-email"
            ),
            operationKey = "",
            amount = java.math.BigDecimal("-100.00"),
            description = "",
            timestamp = java.time.Instant.now()
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

// === ARCHIVO: src/test/kotlin/com/pragma/microservice/infrastructure/client/ExternalInfoClientTest.kt ===
package com.pragma.microservice.infrastructure.client

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
    fun `getInfo debe retornar informacion exitosa cuando el servicio responde correctamente`() = runTest {
        val requestId = "req-123"
        val expectedResponse = ExternalInfoResponse(
            id = "info-456",
            requestId = requestId,
            additionalData = mapOf("key" to "value"),
            timestamp = java.time.Instant.now().toString()
        )

        coEvery {
            httpClient.get("${client.baseUrl}/info/$requestId")
        } returns HttpResponseMock(expectedResponse)

        val result = client.getInfo(requestId)

        assertTrue(result.isSuccess)
        assertEquals("info-456", result.getOrNull()?.id)
        coVerify(exactly = 1) { httpClient.get("${client.baseUrl}/info/$requestId") }
    }

    @Test
    fun `getInfo debe manejar error 404 del servicio externo`() = runTest {
        val requestId = "nonexistent"

        coEvery {
            httpClient.get("${client.baseUrl}/info/$requestId")
        } throws ExternalServiceException(
            "Resource not found",
            HttpStatusCode.NotFound.value
        )

        val result = client.getInfo(requestId)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ExternalServiceException)
    }

    @Test
    fun `getInfo debe manejar error 500 del servicio externo`() = runTest {
        val requestId = "req-500"

        coEvery {
            httpClient.get("${client.baseUrl}/info/$requestId")
        } throws ExternalServiceException(
            "Internal server error",
            HttpStatusCode.InternalServerError.value
        )

        val result = client.getInfo(requestId)

        assertTrue(result.isFailure)
    }

    @Test
    fun `getInfo debe manejar timeout del servicio externo`() = runTest {
        val requestId = "req-timeout"

        coEvery {
            httpClient.get("${client.baseUrl}/info/$requestId")
        } throws ExternalServiceException(
            "Request timeout",
            HttpStatusCode.RequestTimeout.value
        )

        val result = client.getInfo(requestId)

        assertTrue(result.isFailure)
    }

    @Test
    fun `getInfo debe manejar exception de red`() = runTest {
        val requestId = "req-network-error"

        coEvery {
            httpClient.get("${client.baseUrl}/info/$requestId")
        } throws ExternalServiceException(
            "Network error",
            0
        )

        val result = client.getInfo(requestId)

        assertTrue(result.isFailure)
    }

    @Test
    fun `getInfo debe lanzar exception para requestId vacio`() = runTest {
        val result = client.getInfo("")

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun `getInfo debe lanzar exception para requestId nulo`() = runTest {
        val result = client.getInfo(null)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
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

// === ARCHIVO: src/test/kotlin/com/pragma/microservice/infrastructure/idempotency/IdempotencyRepositoryTest.kt ===
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


// === ARCHIVO: src/main/kotlin/com/pragma/microservice/Application.kt ===
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

// === ARCHIVO: src/main/kotlin/com/pragma/microservice/application/RequestProcessor.kt ===
package com.pragma.microservice.application

import com.pragma.microservice.domain.ClientInfo
import com.pragma.microservice.domain.ClientType
import com.pragma.microservice.domain.IdempotencyResult
import com.pragma.microservice.domain.OperationKey
import com.pragma.microservice.domain.OperationKeyStatus
import com.pragma.microservice.domain.Request
import com.pragma.microservice.domain.RequestStatus
import com.pragma.microservice.domain.RequestSummary
import com.pragma.microservice.domain.ValidationResult
import com.pragma.microservice.infrastructure.audit.AuditService
import com.pragma.microservice.infrastructure.client.ExternalInfoClient
import com.pragma.microservice.infrastructure.idempotency.IdempotencyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class RequestProcessor : KoinComponent {

    private val idempotencyRepository: IdempotencyRepository by inject()
    private val externalInfoClient: ExternalInfoClient by inject()
    private val auditService: AuditService by inject()

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(RequestProcessor::class.java)
        private const val MAX_RETRIES = 3
        private const val TIMEOUT_SECONDS = 30L
    }

    @OptIn(KoinExperimentalAPI::class)
    suspend fun processRequest(request: Request, operationKey: String): RequestSummary {
        return withContext(Dispatchers.IO) {
            logger.info("Processing request: ${request.id} with operation key: $operationKey")

            val idempotencyResult = validateIdempotency(operationKey, request.id)
            
            if (idempotencyResult.isNew && idempotencyResult.existingKey != null) {
                logger.info("Returning cached result for operation key: $operationKey")
                return@withContext buildSummaryFromCachedKey(idempotencyResult.existingKey)
            }

            val validationResult = request.validate()
            if (validationResult is ValidationResult.Invalid) {
                logger.warn("Request validation failed: ${validationResult.reason}")
                handleValidationFailure(request, operationKey, validationResult.reason)
                return@withContext RequestSummary(
                    requestId = request.id,
                    status = RequestStatus.Rejected(validationResult.reason),
                    processedAt = java.time.Instant.now(),
                    externalInfo = null,
                    errorMessage = validationResult.reason
                )
            }

            val processingRequest = request.markAsProcessing()
            updateRequestInIdempotency(operationKey, processingRequest)

            try {
                val externalInfo = fetchExternalInfo(request.clientInfo)
                val completedRequest = processingRequest.markAsCompleted()
                
                updateRequestInIdempotency(operationKey, completedRequest)
                markIdempotencyAsConsumed(operationKey, request.id)
                
                emitAuditEvent(completedRequest, "REQUEST_COMPLETED")
                
                logger.info("Request ${request.id} processed successfully")
                RequestSummary(
                    requestId = request.id,
                    status = RequestStatus.Completed,
                    processedAt = java.time.Instant.now(),
                    externalInfo = externalInfo,
                    errorMessage = null
                )
            } catch (e: Exception) {
                logger.error("Error processing request ${request.id}: ${e.message}", e)
                handleProcessingError(request, operationKey, e)
            }
        }
    }

    private suspend fun validateIdempotency(operationKey: String, requestId: String): IdempotencyResult {
        return try {
            idempotencyRepository.validateAndMark(operationKey, requestId)
        } catch (e: Exception) {
            logger.error("Error validating idempotency: ${e.message}", e)
            IdempotencyResult(isNew = true, existingKey = null, wasConsumed = false)
        }
    }

    private suspend fun fetchExternalInfo(clientInfo: ClientInfo): Map<String, Any> {
        return try {
            val clientType = clientInfo.clientType.name
            val clientId = clientInfo.clientId
            
            logger.debug("Fetching external info for client type: $clientType, clientId: $clientId")
            
            val response = externalInfoClient.getClientInfo(clientType, clientId)
            
            if (response != null) {
                logger.info("External info retrieved successfully for client: $clientId")
                response
            } else {
                logger.warn("No external info found for client: $clientId")
                emptyMap()
            }
        } catch (e: Exception) {
            logger.error("Failed to fetch external info: ${e.message}", e)
            throw e
        }
    }

    private suspend fun updateRequestInIdempotency(operationKey: String, request: Request) {
        try {
            val existingKey = idempotencyRepository.getKey(operationKey)
            val updatedKey = if (existingKey != null) {
                existingKey.copy(
                    requestData = serializeRequest(request),
                    status = mapRequestStatusToKeyStatus(request.status)
                )
            } else {
                OperationKey(
                    key = operationKey,
                    requestId = request.id,
                    requestData = serializeRequest(request),
                    status = mapRequestStatusToKeyStatus(request.status),
                    createdAt = java.time.Instant.now(),
                    expiresAt = java.time.Instant.now().plusSeconds(TIMEOUT_SECONDS * 2)
                )
            }
            idempotencyRepository.saveKey(updatedKey)
        } catch (e: Exception) {
            logger.error("Error updating idempotency record: ${e.message}", e)
        }
    }

    private suspend fun markIdempotencyAsConsumed(operationKey: String, requestId: String) {
        try {
            idempotencyRepository.markAsConsumed(operationKey)
            logger.debug("Operation key $operationKey marked as consumed")
        } catch (e: Exception) {
            logger.error("Error marking idempotency as consumed: ${e.message}", e)
        }
    }

    private suspend fun handleValidationFailure(
        request: Request,
        operationKey: String,
        reason: String
    ) {
        val rejectedRequest = request.reject(reason)
        updateRequestInIdempotency(operationKey, rejectedRequest)
        emitAuditEvent(rejectedRequest, "REQUEST_REJECTED")
    }

    private suspend fun handleProcessingError(
        request: Request,
        operationKey: String,
        error: Exception
    ) {
        val failedRequest = request.markAsFailed()
        updateRequestInIdempotency(operationKey, failedRequest)
        emitAuditEvent(failedRequest, "REQUEST_FAILED")
    }

    private suspend fun emitAuditEvent(request: Request, eventType: String) {
        try {
            auditService.emitEvent(
                eventType = eventType,
                requestId = request.id,
                status = request.status.name,
                clientInfo = request.clientInfo
            )
        } catch (e: Exception) {
            logger.error("Error emitting audit event: ${e.message}", e)
        }
    }

    private fun buildSummaryFromCachedKey(operationKey: OperationKey): RequestSummary {
        val request = deserializeRequest(operationKey.requestData)
        val status = when (operationKey.status) {
            OperationKeyStatus.USED -> RequestStatus.Completed
            OperationKeyStatus.EXPIRED -> RequestStatus.Failed
            OperationKeyStatus.PENDING -> RequestStatus.Processing
            OperationKeyStatus.CONSUMED -> RequestStatus.Completed
        }
        
        return RequestSummary(
            requestId = request.id,
            status = status,
            processedAt = operationKey.createdAt,
            externalInfo = null,
            errorMessage = null
        )
    }

    private fun mapRequestStatusToKeyStatus(requestStatus: RequestStatus): OperationKeyStatus {
        return when (requestStatus) {
            is RequestStatus.Pending -> OperationKeyStatus.PENDING
            is RequestStatus.Processing -> OperationKeyStatus.PENDING
            is RequestStatus.Completed -> OperationKeyStatus.USED
            is RequestStatus.Failed -> OperationKeyStatus.EXPIRED
            is RequestStatus.Rejected -> OperationKeyStatus.EXPIRED
        }
    }

    private fun serializeRequest(request: Request): String {
        return """{"id":"${request.id}","clientId":"${request.clientInfo.clientId}","clientType":"${request.clientInfo.clientType.name}","amount":${request.amount}}"""
    }

    private fun deserializeRequest(data: String): Request {
        val json = kotlinx.serialization.json.Json.parseToJsonElement(data)
        val obj = json.jsonObject
        return Request(
            id = obj["id"]?.jsonPrimitive?.content ?: "",
            clientInfo = ClientInfo(
                clientId = obj["clientId"]?.jsonPrimitive?.content ?: "",
                clientType = ClientType.valueOf(obj["clientType"]?.jsonPrimitive?.content ?: "INDIVIDUAL")
            ),
            amount = obj["amount"]?.jsonPrimitive?.content?.toBigDecimal() ?: java.math.BigDecimal.ZERO
        )
    }
}

// === ARCHIVO: src/main/kotlin/com/pragma/microservice/infrastructure/audit/AuditService.kt ===
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


// === ARCHIVO: src/main/kotlin/com/pragma/microservice/infrastructure/exception/GlobalExceptionHandler.kt ===
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

// === ARCHIVO: src/main/kotlin/com/pragma/microservice/infrastructure/Controller.kt ===
package com.pragma.microservice.infrastructure

import com.pragma.microservice.application.RequestProcessor
import com.pragma.microservice.domain.ClientType
import com.pragma.microservice.domain.Completed
import com.pragma.microservice.domain.Invalid
import com.pragma.microservice.domain.Pending
import com.pragma.microservice.domain.Processing
import com.pragma.microservice.domain.Request
import com.pragma.microservice.domain.RequestStatus
import com.pragma.microservice.domain.ValidationResult
import com.pragma.microservice.infrastructure.client.ExternalInfoClient
import com.pragma.microservice.infrastructure.exception.ErrorResponse
import com.pragma.microservice.infrastructure.exception.RateLimitExceededException
import com.pragma.microservice.infrastructure.audit.AuditService
import io.ktor.http.HttpStatusCode
import io.ktor.http.HttpHeaders
import io.ktor.http.ContentType
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.plugins.requestvalidation.RequestValidationException
import io.ktor.server.plugins.requestvalidation.body
import io.ktor.server.plugins.requestvalidation.validate
import io.ktor.server.request.header
import io.ktor.server.request.path
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.delete
import io.ktor.server.routing.route
import io.ktor.utils.io.jvm.javaio.toInputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

private val logger: Logger = LoggerFactory.getLogger(Controller::class.java)

@Serializable
data class ProcessRequestInput(
    val clientId: String,
    val clientType: String,
    val operationKey: String,
    val requestData: String,
    val priority: Int = 0
)

@Serializable
data class ProcessResponse(
    val requestId: String,
    val status: String,
    val message: String,
    val processingTimeMs: Long? = null
)

@Serializable
data class HealthResponse(
    val status: String,
    val timestamp: Long,
    val uptimeSeconds: Long,
    val requestsProcessed: Int,
    val activeRequests: Int
)

@Serializable
data class MetricsResponse(
    val totalRequests: Int,
    val successfulRequests: Int,
    val failedRequests: Int,
    val averageProcessingTimeMs: Double,
    val requestsPerSecond: Double
)

class MicrosservicioController : KoinComponent {
    private val requestProcessor: RequestProcessor by inject()
    private val externalInfoClient: ExternalInfoClient by inject()
    private val auditService: AuditService by inject()
    
    private val requestCount = AtomicInteger(0)
    private val successCount = AtomicInteger(0)
    private val failureCount = AtomicInteger(0)
    private val processingTimes = ConcurrentHashMap<String, Long>()
    private val activeRequests = ConcurrentHashMap<String, Long>()
    private var applicationStartTime = System.currentTimeMillis()
    private val rateLimitMap = ConcurrentHashMap<String, Long>()
    
    private val rateLimitWindowMs = 60_000L
    private val maxRequestsPerWindow = 1000
    
    suspend fun processRequest(call: ApplicationCall) {
        val startTime = System.currentTimeMillis()
        val requestId = UUID.randomUUID().toString()
        
        try {
            val clientId = call.request.header("X-Client-Id") 
                ?: throw RequestValidationException(listOf("X-Client-Id header is required"))
            
            val idempotencyKey = call.request.header("X-Idempotency-Key")
            
            if (!checkRateLimit(clientId)) {
                throw RateLimitExceededException(
                    "Rate limit exceeded for client: $clientId",
                    retryAfterSeconds = 60
                )
            }
            
            activeRequests[requestId] = System.currentTimeMillis()
            requestCount.incrementAndGet()
            
            val input = call.receive<ProcessRequestInput>()
            
            logger.info("Processing request $requestId for client $clientId with operation key: ${input.operationKey}")
            
            val request = Request(
                id = requestId,
                clientId = input.clientId,
                clientType = ClientType.valueOf(input.clientType),
                operationKey = input.operationKey,
                requestData = input.requestData,
                priority = input.priority,
                status = Pending
            )
            
            val validation = request.validate()
            if (validation is Invalid) {
                logger.warn("Request validation failed: ${validation.reason}")
                call.respond(
                    HttpStatusCode.BadRequest,
                    ProcessResponse(
                        requestId = requestId,
                        status = "REJECTED",
                        message = validation.reason
                    )
                )
                return
            }
            
            val result = requestProcessor.processRequest(request, idempotencyKey)
            
            val processingTime = System.currentTimeMillis() - startTime
            processingTimes[requestId] = processingTime
            activeRequests.remove(requestId)
            
            if (result.status == Completed) {
                successCount.incrementAndGet()
            } else {
                failureCount.incrementAndGet()
            }
            
            logger.info("Request $requestId processed in ${processingTime}ms with status: ${result.status}")
            
            auditService.emitEvent(
                requestId = requestId,
                eventType = "REQUEST_PROCESSED",
                clientId = clientId,
                details = mapOf(
                    "status" to result.status.toString(),
                    "processingTimeMs" to processingTime
                )
            )
            
            call.respond(
                HttpStatusCode.OK,
                ProcessResponse(
                    requestId = requestId,
                    status = result.status.toString(),
                    message = "Request processed successfully",
                    processingTimeMs = processingTime
                )
            )
        } catch (e: Exception) {
            activeRequests.remove(requestId)
            failureCount.incrementAndGet()
            logger.error("Error processing request $requestId: ${e.message}", e)
            throw e
        }
    }
    
    suspend fun getRequestStatus(call: ApplicationCall) {
        val requestId = call.parameters["requestId"] 
            ?: throw RequestValidationException(listOf("requestId parameter is required"))
        
        logger.debug("Getting status for request: $requestId")
        
        val status = requestProcessor.getRequestStatus(requestId)
        
        if (status != null) {
            call.respond(HttpStatusCode.OK, status)
        } else {
            call.respond(
                HttpStatusCode.NotFound,
                ErrorResponse(
                    status = HttpStatusCode.NotFound.value,
                    error = "REQUEST_NOT_FOUND",
                    message = "Request with id $requestId not found",
                    path = call.request.path()
                )
            )
        }
    }
    
    suspend fun getHealth(call: ApplicationCall) {
        val uptimeSeconds = (System.currentTimeMillis() - applicationStartTime) / 1000
        
        call.respond(
            HttpStatusCode.OK,
            HealthResponse(
                status = "UP",
                timestamp = System.currentTimeMillis(),
                uptimeSeconds = uptimeSeconds,
                requestsProcessed = requestCount.get(),
                activeRequests = activeRequests.size
            )
        )
    }
    
    suspend fun getMetrics(call: ApplicationCall) {
        val avgTime = if (processingTimes.isNotEmpty()) {
            processingTimes.values.average()
        } else 0.0
        
        val uptimeSeconds = (System.currentTimeMillis() - applicationStartTime) / 1000
        val rps = if (uptimeSeconds > 0) {
            requestCount.get().toDouble() / uptimeSeconds
        } else 0.0
        
        call.respond(
            HttpStatusCode.OK,
            MetricsResponse(
                totalRequests = requestCount.get(),
                successfulRequests = successCount.get(),
                failedRequests = failureCount.get(),
                averageProcessingTimeMs = avgTime,
                requestsPerSecond = rps
            )
        )
    }
    
    suspend fun getExternalInfo(call: ApplicationCall) {
        val infoId = call.parameters["infoId"]
            ?: throw RequestValidationException(listOf("infoId parameter is required"))
        
        logger.debug("Fetching external info for id: $infoId")
        
        val info = withContext(Dispatchers.IO) {
            externalInfoClient.getClientInfo("DEFAULT", infoId)
        }
        
        if (info != null) {
            call.respond(HttpStatusCode.OK, info)
        } else {
            call.respond(
                HttpStatusCode.NotFound,
                ErrorResponse(
                    status = HttpStatusCode.NotFound.value,
                    error = "INFO_NOT_FOUND",
                    message = "External info with id $infoId not found",
                    path = call.request.path()
                )
            )
        }
    }
    
    suspend fun processBatch(call: ApplicationCall) {
        val requests = call.receive<List<ProcessRequestInput>>()
        
        logger.info("Processing batch of ${requests.size} requests")
        
        val results = withContext(Dispatchers.IO) {
            requests.map { input ->
                async {
                    try {
                        val requestId = UUID.randomUUID().toString()
                        val request = Request(
                            id = requestId,
                            clientId = input.clientId,
                            clientType = ClientType.valueOf(input.clientType),
                            operationKey = input.operationKey,
                            requestData = input.requestData,
                            priority = input.priority,
                            status = Pending
                        )
                        
                        requestProcessor.processRequest(request, null)
                    } catch (e: Exception) {
                        logger.error("Error processing batch request: ${e.message}", e)
                        null
                    }
                }
            }.awaitAll()
        }
        
        val successCount = results.count { it != null }
        val failCount = results.size - successCount
        
        logger.info("Batch processing complete: $successCount succeeded, $failCount failed")
        
        call.respond(
            HttpStatusCode.OK,
            mapOf(
                "total" to results.size,
                "succeeded" to successCount,
                "failed" to failCount
            )
        )
    }
    
    private fun checkRateLimit(clientId: String): Boolean {
        val now = System.currentTimeMillis()
        val windowStart = now - rateLimitWindowMs
        
        val clientRequests = rateLimitMap.entries
            .filter { it.value > windowStart }
            .toMutableList()
        
        val recentCount = clientRequests.size
        
        if (recentCount >= maxRequestsPerWindow) {
            logger.warn("Rate limit exceeded for client: $clientId")
            return false
        }
        
        rateLimitMap[clientId] = now
        
        clientRequests.forEach { (key, timestamp) ->
            if (timestamp < windowStart) {
                rateLimitMap.remove(key)
            }
        }
        
        return true
    }
}

fun Route.microservicioRoutes() {
    val controller = MicrosservicioController()
    
    route("/api/v1/requests") {
        post {
            controller.processRequest(call)
        }
        
        get("/{requestId}") {
            controller.getRequestStatus(call)
        }
        
        post("/batch") {
            controller.processBatch(call)
        }
    }
    
    route("/api/v1/health") {
        get {
            controller.getHealth(call)
        }
    }
    
    route("/api/v1/metrics") {
        get {
            controller.getMetrics(call)
        }
    }
    
    route("/api/v1/external") {
        get("/{infoId}") {
            controller.getExternalInfo(call)
        }
    }
}

// === ARCHIVO: src/main/kotlin/com/pragma/microservice/application/RequestProcessor.kt ===
package com.pragma.microservice.application

import com.pragma.microservice.domain.ClientType
import com.pragma.microservice.domain.ClientInfo
import com.pragma.microservice.domain.IdempotencyResult
import com.pragma.microservice.domain.Invalid
import com.pragma.microservice.domain.OperationKey
import com.pragma.microservice.domain.OperationKeyStatus
import com.pragma.microservice.domain.Request
import com.pragma.microservice.domain.RequestSummary
import com.pragma.microservice.domain.RequestStatus
import com.pragma.microservice.domain.Valid
import com.pragma.microservice.domain.ValidationResult
import com.pragma.microservice.infrastructure.client.ExternalInfoClient
import com.pragma.microservice.infrastructure.idempotency.IdempotencyRepository
import com.pragma.microservice.infrastructure.audit.AuditEvent
import com.pragma.microservice.infrastructure.audit.AuditService
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.retry.Retry
import io.github.resilience4j.bulkhead.ThreadPoolBulkhead
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Instant

class RequestProcessor : KoinComponent {
    private val externalInfoClient: ExternalInfoClient by inject()
    private val idempotencyRepository: IdempotencyRepository by inject()
    private val auditService: AuditService by inject()
    
    private val logger: Logger = LoggerFactory.getLogger(RequestProcessor::class.java)
    
    private val requestCache = mutableMapOf<String, RequestSummary>()
    
    suspend fun processRequest(request: Request?, operationKey: String?): RequestSummary {
        if (request == null) {
            return RequestSummary(
                requestId = "unknown",
                status = RequestStatus.Failed,
                message = "Request cannot be null",
                timestamp = Instant.now()
            )
        }
        
        val validation = request.validate()
        if (validation is Invalid) {
            return handleValidationFailure(request, validation)
        }
        
        if (!request.canBeProcessed()) {
            return RequestSummary(
                requestId = request.id,
                status = RequestStatus.Rejected("Request cannot be processed in current state"),
                message = validation.toString(),
                timestamp = Instant.now()
            )
        }
        
        val idempotencyKey = operationKey ?: request.id
        val idempotencyResult = validateIdempotency(idempotencyKey, request.id)
        
        if (!idempotencyResult.isNew) {
            logger.info("Returning cached result for idempotency key: $idempotencyKey")
            return buildSummaryFromCachedKey(idempotencyResult.existingKey!!)
        }
        
        val processingRequest = request.markAsProcessing()
        
        try {
            val clientInfo = fetchExternalInfo(processingRequest.clientInfo)
            
            val completedRequest = processingRequest.markAsCompleted()
            
            updateRequestInIdempotency(idempotencyKey, completedRequest)
            
            emitAuditEvent(completedRequest, "REQUEST_COMPLETED")
            
            val summary = RequestSummary(
                requestId = completedRequest.id,
                status = completedRequest.status,
                message = "Request processed successfully",
                timestamp = Instant.now()
            )
            
            requestCache[completedRequest.id] = summary
            
            return summary
        } catch (e: Exception) {
            return handleProcessingError(request, e)
        }
    }
    
    suspend fun getRequestStatus(requestId: String): RequestSummary? {
        return requestCache[requestId]
    }
    
    private suspend fun validateIdempotency(operationKey: String, requestId: String): IdempotencyResult {
        return withContext(Dispatchers.IO) {
            idempotencyRepository.validateAndMark(operationKey, requestId)
        }
    }
    
    private suspend fun fetchExternalInfo(clientInfo: ClientInfo): Map<String, Any> {
        return withContext(Dispatchers.IO) {
            try {
                val clientInfoMap = externalInfoClient.getClientInfo(
                    clientType = clientInfo.type.name,
                    clientId = clientInfo.id
                )
                clientInfoMap ?: emptyMap()
            } catch (e: Exception) {
                logger.warn("Failed to fetch external info: ${e.message}")
                emptyMap()
            }
        }
    }
    
    private suspend fun updateRequestInIdempotency(operationKey: String, request: Request) {
        withContext(Dispatchers.IO) {
            val key = idempotencyRepository.getKey(operationKey)
            if (key != null) {
                val updatedKey = key.markAsUsed(request.id)
                idempotencyRepository.saveKey(updatedKey)
            }
        }
    }
    
    private suspend fun markIdempotencyAsConsumed(operationKey: String, requestId: String) {
        withContext(Dispatchers.IO) {
            idempotencyRepository.markAsConsumed(operationKey)
        }
    }
    
    private suspend fun handleValidationFailure(
        request: Request,
        validation: Invalid
    ): RequestSummary {
        logger.warn("Validation failed for request ${request.id}: ${validation.reason}")
        
        emitAuditEvent(request, "VALIDATION_FAILED")
        
        return RequestSummary(
            requestId = request.id,
            status = RequestStatus.Rejected(validation.reason),
            message = validation.reason,
            timestamp = Instant.now()
        )
    }
    
    private suspend fun handleProcessingError(
        request: Request,
        error: Exception
    ): RequestSummary {
        logger.error("Error processing request ${request.id}: ${error.message}", error)
        
        emitAuditEvent(request, "PROCESSING_ERROR")
        
        return RequestSummary(
            requestId = request.id,
            status = RequestStatus.Failed,
            message = "Error processing request: ${error.message}",
            timestamp = Instant.now()
        )
    }
    
    private suspend fun emitAuditEvent(request: Request, eventType: String) {
        try {
            auditService.emitEvent(
                requestId = request.id,
                eventType = eventType,
                clientId = request.clientId,
                details = mapOf(
                    "clientType" to request.clientType.name,
                    "operationKey" to request.operationKey
                )
            )
        } catch (e: Exception) {
            logger.warn("Failed to emit audit event: ${e.message}")
        }
    }
    
    private fun buildSummaryFromCachedKey(operationKey: OperationKey): RequestSummary {
        val cachedRequestId = operationKey.processedRequestId ?: "unknown"
        
        val summary = RequestSummary(
            requestId = cachedRequestId,
            status = mapRequestStatusToKeyStatus(operationKey.status),
            message = "Result from previous request",
            timestamp = operationKey.createdAt
        )
        
        requestCache[cachedRequestId] = summary
        
        return summary
    }
    
    private fun mapRequestStatusToKeyStatus(keyStatus: OperationKeyStatus): RequestStatus {
        return when (keyStatus) {
            OperationKeyStatus.AVAILABLE -> RequestStatus.Pending
            OperationKeyStatus.CONSUMED -> RequestStatus.Completed
            OperationKeyStatus.EXPIRED -> RequestStatus.Failed
            OperationKeyStatus.EXHAUSTED -> RequestStatus.Failed
        }
    }
    
    private fun serializeRequest(request: Request): String {
        return "${request.id}|${request.clientId}|${request.operationKey}|${request.status}"
    }
    
    private fun deserializeRequest(data: String): Request? {
        return try {
            val parts = data.split("|")
            if (parts.size >= 4) {
                Request(
                    id = parts[0],
                    clientId = parts[1],
                    clientType = ClientType.STANDARD,
                    operationKey = parts[2],
                    requestData = "",
                    priority = 0,
                    status = RequestStatus.Pending
                )
            } else null
        } catch (e: Exception) {
            null
        }
    }
}

// === ARCHIVO: src/test/kotlin/com/pragma/microservice/application/RequestProcessorTest.kt ===
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

// === ARCHIVO: src/test/kotlin/com/pragma/microservice/infrastructure/client/ExternalInfoClientTest.kt ===
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

// === ARCHIVO: src/main/kotlin/com/pragma/microservice/domain/OperationKey.kt ===
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

// === ARCHIVO: src/main/kotlin/com/pragma/microservice/infrastructure/idempotency/IdempotencyRepository.kt ===
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
```
