# AGENTS.md

Instrucciones para el agente de IA que abra este repositorio (Claude Code, Cursor, Codex, Copilot, Gemini). Se cargan solas: no hay que pegar nada en ningun chat.

## Que es este repositorio

Es el codigo base de un reto de aprendizaje de Pragma: **Implementación de un microservicio resiliente en Kotlin**.

| | |
|---|---|
| Tema | Microservicios con Kotlin |
| Nivel | junior-l2 |
| Chapter | Generico |
| Especialidad | Inferido del contexto |
| Stack | Kotlin 1.9 / Ktor 2.3.12 |
| Patron arquitectonico | microservicio reactivo con capas hexagonal |
| Tiempo estimado | 8 horas |

## Tu tarea

Dejar este proyecto en estado **verificable**: que el comando de verificacion corra sin errores. Escribi los archivos en disco, en este repositorio. No generes ZIPs ni archivos adjuntos.

En orden:

1. Corre `el comando de build o arranque canonico del stack elegido` y mira que falla.
2. Completa lo que falte de la lista de abajo: manifiesto de dependencias, punto de entrada, capa de interfaz y las capas del patron declarado.
3. Arregla SOLO los errores que impiden compilar o arrancar.
4. Volve a correr `el comando de build o arranque canonico del stack elegido` hasta que pase.
5. Pará ahí.

## Regla dura: las fases son trabajo del humano

**PROHIBIDO implementar los entregables de las fases.** El valor del reto esta en que la persona los resuelva. Tu trabajo es que tenga un proyecto que arranca; el hueco pedagogico se queda como esta.

No resuelvas nada de esto:

- **Fase 1 — Definición y configuración inicial**: Especificación de las interfaces y operaciones del microservicio, junto con los umbrales de rendimiento y disponibilidad.
- **Fase 2 — Implementación de la lógica de negocio**: Microservicio con la lógica de negocio implementada, incluyendo la interacción con el'servicio externo de información', el manejo de la idempotencia y la emisión de eventos al 'sistema de auditoría'.
- **Fase 3 — Optimización y pruebas**: Microservicio optimizado y probado, que cumple con los umbrales de rendimiento y disponibilidad establecidos.

Distincion operativa:

- **Arreglar** (si): import faltante, tipo que no existe, dependencia sin declarar, error de sintaxis, archivo referenciado que no existe.
- **No tocar** (no): logica de negocio incompleta, validaciones ausentes, secretos hardcodeados, APIs deprecadas que funcionan, concurrencia insegura, patrones mejorables. Eso es lo que la persona tiene que encontrar.

## Lo que falta y tenes que completar

### 1. Boilerplate del stack (3)

Sin esto el proyecto no compila ni arranca. **Es tu trabajo crearlo**, y no toca nada de lo pedagogico: es andamiaje del stack.

- [ ] **Manifiesto de dependencias del stack elegido** — Sin un manifiesto de dependencias reconocible, ninguna herramienta de build sabe que instalar y el proyecto no arranca.
- [ ] **Punto de entrada del stack elegido** — Sin un punto de entrada reconocible, el runtime no tiene por donde arrancar la aplicacion.
- [ ] **Capa de interfaz (controller/handler)** — Sin una capa de interfaz explicita, no hay forma de invocar la logica de negocio desde afuera del proceso.

### 2. Referencias colgando (22)

Salieron de un analisis estatico del codigo que SI esta en el repo. Cada una rompe la compilacion:

- [ ] `src/main/kotlin/com/pragma/microservice/Application.kt` — `com.pragma.microservice.infrastructure.exception.GlobalExceptionHandler`
      El import com.pragma.microservice.infrastructure.exception.GlobalExceptionHandler usa un paquete propio del proyecto pero ningun archivo generado declara ese tipo. Falta generar la clase/interfaz, o el import esta mal escrito.
- [ ] `src/main/kotlin/com/pragma/microservice/application/RequestProcessor.kt` — `Failed`
      Failed se usa en el cuerpo del archivo pero no esta importado. El proyecto lo declara en com.pragma.microservice.domain.Failed.
- [ ] `src/main/kotlin/com/pragma/microservice/application/RequestProcessor.kt` — `Rejected`
      Rejected se usa en el cuerpo del archivo pero no esta importado. El proyecto lo declara en com.pragma.microservice.domain.Rejected.
- [ ] `src/main/kotlin/com/pragma/microservice/application/RequestProcessor.kt` — `Pending`
      Pending se usa en el cuerpo del archivo pero no esta importado. El proyecto lo declara en com.pragma.microservice.domain.Pending.
- [ ] `src/main/kotlin/com/pragma/microservice/application/RequestProcessor.kt` — `Completed`
      Completed se usa en el cuerpo del archivo pero no esta importado. El proyecto lo declara en com.pragma.microservice.domain.Completed.
- [ ] `src/main/kotlin/com/pragma/microservice/infrastructure/audit/AuditService.kt` — `Failed`
      Failed se usa en el cuerpo del archivo pero no esta importado. El proyecto lo declara en com.pragma.microservice.domain.Failed.
- [ ] `src/test/kotlin/com/pragma/microservice/application/RequestProcessorTest.kt` — `Completed`
      Completed se usa en el cuerpo del archivo pero no esta importado. El proyecto lo declara en com.pragma.microservice.domain.Completed.
- [ ] `src/test/kotlin/com/pragma/microservice/application/RequestProcessorTest.kt` — `Rejected`
      Rejected se usa en el cuerpo del archivo pero no esta importado. El proyecto lo declara en com.pragma.microservice.domain.Rejected.
- [ ] `src/test/kotlin/com/pragma/microservice/application/RequestProcessorTest.kt` — `Failed`
      Failed se usa en el cuerpo del archivo pero no esta importado. El proyecto lo declara en com.pragma.microservice.domain.Failed.
- [ ] `src/test/kotlin/com/pragma/microservice/application/RequestProcessorTest.kt` — `Pending`
      Pending se usa en el cuerpo del archivo pero no esta importado. El proyecto lo declara en com.pragma.microservice.domain.Pending.
- [ ] `src/main/kotlin/com/pragma/microservice/application/RequestProcessor.kt` — `Valid`
      El import com.pragma.microservice.domain.Valid no se usa en ningun lado del cuerpo del archivo. Se puede eliminar.
- [ ] `src/main/kotlin/com/pragma/microservice/application/RequestProcessor.kt` — `ValidationResult`
      El import com.pragma.microservice.domain.ValidationResult no se usa en ningun lado del cuerpo del archivo. Se puede eliminar.
- [ ] `src/main/kotlin/com/pragma/microservice/application/RequestProcessor.kt` — `AuditEvent`
      El import com.pragma.microservice.infrastructure.audit.AuditEvent no se usa en ningun lado del cuerpo del archivo. Se puede eliminar.
- [ ] `src/main/kotlin/com/pragma/microservice/infrastructure/audit/AuditService.kt` — `RequestStatus`
      El import com.pragma.microservice.domain.RequestStatus no se usa en ningun lado del cuerpo del archivo. Se puede eliminar.
- [ ] `src/main/kotlin/com/pragma/microservice/infrastructure/Controller.kt` — `RequestStatus`
      El import com.pragma.microservice.domain.RequestStatus no se usa en ningun lado del cuerpo del archivo. Se puede eliminar.
- [ ] `src/main/kotlin/com/pragma/microservice/infrastructure/Controller.kt` — `ValidationResult`
      El import com.pragma.microservice.domain.ValidationResult no se usa en ningun lado del cuerpo del archivo. Se puede eliminar.
- [ ] `src/test/kotlin/com/pragma/microservice/application/RequestProcessorTest.kt` — `ClientInfo`
      El import com.pragma.microservice.domain.ClientInfo no se usa en ningun lado del cuerpo del archivo. Se puede eliminar.
- [ ] `src/test/kotlin/com/pragma/microservice/application/RequestProcessorTest.kt` — `RequestSummary`
      El import com.pragma.microservice.domain.RequestSummary no se usa en ningun lado del cuerpo del archivo. Se puede eliminar.
- [ ] `src/test/kotlin/com/pragma/microservice/infrastructure/client/ExternalInfoClientTest.kt` — `Request`
      El import com.pragma.microservice.domain.Request no se usa en ningun lado del cuerpo del archivo. Se puede eliminar.
- [ ] `src/main/kotlin/com/pragma/microservice/infrastructure/idempotency/IdempotencyRepository.kt` — `OperationKey.copy`
      Se invoca `copy` sobre `OperationKey`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/kotlin/com/pragma/microservice/infrastructure/idempotency/IdempotencyRepository.kt` — `OperationKey.replace`
      Se invoca `replace` sobre `OperationKey`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/kotlin/com/pragma/microservice/application/RequestProcessor.kt` — `Invalid.toString`
      Se invoca `toString` sobre `Invalid`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.

### Presentes (14)

- `build.gradle.kts`
- `src/main/kotlin/com/pragma/microservice/Application.kt`
- `src/main/kotlin/com/pragma/microservice/domain/Request.kt`
- `src/main/kotlin/com/pragma/microservice/domain/OperationKey.kt`
- `src/main/kotlin/com/pragma/microservice/infrastructure/idempotency/IdempotencyRepository.kt`
- `src/main/kotlin/com/pragma/microservice/application/RequestProcessor.kt`
- `src/main/kotlin/com/pragma/microservice/infrastructure/client/ExternalInfoClient.kt`
- `src/main/kotlin/com/pragma/microservice/infrastructure/audit/AuditService.kt`
- `src/main/kotlin/com/pragma/microservice/infrastructure/config/ResilienceConfig.kt`
- `src/main/kotlin/com/pragma/microservice/infrastructure/exception/GlobalExceptionHandler.kt`
- `src/main/kotlin/com/pragma/microservice/infrastructure/Controller.kt`
- `src/test/kotlin/com/pragma/microservice/application/RequestProcessorTest.kt`
- `src/test/kotlin/com/pragma/microservice/infrastructure/client/ExternalInfoClientTest.kt`
- `src/test/kotlin/com/pragma/microservice/infrastructure/idempotency/IdempotencyRepositoryTest.kt`

### Capas del patron declarado

Cada una tiene que existir como directorio real con al menos un archivo. Codigo plano en la raiz no satisface el patron.

- `src/main/kotlin/com/pragma/microservice`
- `src/main/kotlin/com/pragma/microservice/application`
- `src/main/kotlin/com/pragma/microservice/domain`
- `src/main/kotlin/com/pragma/microservice/infrastructure`
- `src/main/kotlin/com/pragma/microservice/infrastructure/client`
- `src/main/kotlin/com/pragma/microservice/infrastructure/audit`
- `src/main/kotlin/com/pragma/microservice/infrastructure/config`
- `src/main/kotlin/com/pragma/microservice/infrastructure/exception`
- `src/main/kotlin/com/pragma/microservice/infrastructure/idempotency`
- `src/test/kotlin/com/pragma/microservice`

## Verificacion

```bash
el comando de build o arranque canonico del stack elegido
```

Ese comando pasando es la definicion de "terminado" para vos.

## Convenciones que tenes que respetar

- Un solo ecosistema: no declares librerias de otro lenguaje ni mezcles gestores de paquetes.
- Toda libreria que uses tiene que estar declarada en el manifiesto de dependencias.
- Todo import declarado tiene que usarse; todo tipo usado tiene que existir o venir de una dependencia declarada.
- El patron es **microservicio reactivo con capas hexagonal**: los contratos (interfaces, puertos) los define la capa interna y los implementa la externa, nunca al revés.
- Los archivos que crees llevan implementacion real, no stubs: sin `TODO`, sin cuerpos vacios, sin `// getters y setters`.

## Contexto del candidato

Sirve para calibrar el nivel del codigo, no para resolver las fases.

- Brecha que el reto ataca: Sistema distribuido con Kotlin, Ktor, circuit breakers y observabilidad

---

*Generado por Challenge Generator — Pragma. `README.md` tiene el enunciado completo del reto para la persona. `PROMPT_MEJORA.md` es la variante para pegar en un chat, si se prefiere ese flujo.*
