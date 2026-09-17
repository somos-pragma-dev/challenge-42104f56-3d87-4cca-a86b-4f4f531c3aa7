# Implementación de un microservicio resiliente en Kotlin

La empresa necesita un microservicio que maneje solicitudes de clientes, interactúe con un servicio externo para obtener información adicional y garantice la disponibilidad y la consistencia de los datos. El microservicio debe ser capaz de manejar picos de tráfico y fallos temporales del servicio externo. Los actores involucrados son el 'originador de solicitudes', el'servicio externo de información' y el'sistema de auditoría'. El microservicio debe procesar al menos 1 500 solicitudes por segundo en hora pico y garantizar que las solicitudes repetidas con la misma clave de operación no generen duplicados en el sistema.

## Informacion General

| Campo | Valor |
|-------|-------|
| **Tema** | Microservicios con Kotlin |
| **Nivel** | junior-l2 |
| **Tipo** | practical |
| **Tiempo estimado** | 8 horas |

## Fases del Reto

### Fase 0: Configuración del Proyecto

**Objetivo:** Obtener el proyecto base funcional enviando el Código Base a un asistente de IA, que lo analizará, corregirá errores y generará un ZIP listo para usar.

**Tiempo estimado:** 15-30 minutos

**Instrucciones:**

- Asegúrate de tener instalado para ejecutar el proyecto: JDK 17+, Gradle 8+, IDE con soporte Java.
- Copia todo el contenido del campo **Código Base** de este reto — incluyendo el texto de instrucciones que aparece al inicio.
- Abre un asistente de IA (Claude en claude.ai, ChatGPT o Gemini — se recomienda Claude), pega el contenido copiado en el chat y envíalo.
- El asistente analizará los archivos, corregirá errores y generará un archivo ZIP descargable. Descárgalo y extráelo en la carpeta donde quieras trabajar.
- Ejecuta `gradle build` en la raíz. Si no hay errores, estás listo.

**Entregable:** El proyecto compila/arranca sin errores.

<details>
<summary>Pistas de conocimiento</summary>

- Copia el Código Base completo incluyendo el texto de instrucciones al inicio — esas instrucciones le indican al asistente exactamente qué hacer con los archivos.
- Si el asistente no genera el ZIP automáticamente al terminar el análisis, escríbele: "genera el ZIP ahora".
- Si el proyecto tiene errores al arrancar, comparte el mensaje de error con el mismo asistente para que lo corrija.

</details>

### Fase 1: Definición y configuración inicial

**Objetivo:** Establecer la estructura básica del microservicio y definir las interfaces de comunicación.

**Tiempo estimado:** 2 horas

**Instrucciones:**

- Identificar las interfaces necesarias para interactuar con el 'servicio externo de información' y el 'sistema de auditoría'.
- Definir las operaciones que el microservicio debe soportar, incluyendo la gestión de claves de operación para garantizar la idempotencia.
- Establecer los umbrales de rendimiento y disponibilidad requeridos.

**Entregable:** Especificación de las interfaces y operaciones del microservicio, junto con los umbrales de rendimiento y disponibilidad.

<details>
<summary>Pistas de conocimiento</summary>

- Considera cómo manejar las claves de operación para garantizar la idempotencia en las solicitudes repetidas.
- Piensa en cómo el microservicio puede comunicarse de manera eficiente con el 'servicio externo de información'.

</details>

### Fase 2: Implementación de la lógica de negocio

**Objetivo:** Implementar la lógica de negocio del microservicio, incluyendo la interacción con el 'servicio externo de información' y el manejo de la idempotencia.

**Tiempo estimado:** 3 horas

**Instrucciones:**

- Implementar la lógica para procesar las solicitudes de clientes, interactuar con el'servicio externo de información' y registrar las solicitudes con claves de operación idempotentes.
- Manejar los posibles fallos del 'servicio externo de información' utilizando circuit breakers y garantizar la consistencia de los datos.
- Emitir eventos al 'sistema de auditoría' por cada aceptación de solicitud.

**Entregable:** Microservicio con la lógica de negocio implementada, incluyendo la interacción con el'servicio externo de información', el manejo de la idempotencia y la emisión de eventos al 'sistema de auditoría'.

<details>
<summary>Pistas de conocimiento</summary>

- Considera cómo manejar los fallos temporales del'servicio externo de información' utilizando circuit breakers.
- Piensa en cómo garantizar la consistencia de los datos ante posibles fallos.

</details>

### Fase 3: Optimización y pruebas

**Objetivo:** Optimizar el rendimiento del microservicio y realizar pruebas exhaustivas para garantizar su funcionamiento correcto.

**Tiempo estimado:** 3 horas

**Instrucciones:**

- Optimizar el rendimiento del microservicio para manejar al menos 1 500 solicitudes por segundo en hora pico.
- Realizar pruebas unitarias y de integración para garantizar la correcta implementación de la lógica de negocio y el manejo de fallos.
- Verificar que el microservicio cumple con los umbrales de rendimiento y disponibilidad establecidos.

**Entregable:** Microservicio optimizado y probado, que cumple con los umbrales de rendimiento y disponibilidad establecidos.

<details>
<summary>Pistas de conocimiento</summary>

- Considera técnicas de optimización para mejorar el rendimiento del microservicio.
- Piensa en cómo realizar pruebas exhaustivas para garantizar el funcionamiento correcto del microservicio.

</details>

## Dimensiones Evaluadas

- **queEs**: ¿Qué es un microservicio y cuál es su propósito en este escenario?
- **paraQueSirve**: ¿Para qué sirve la idempotencia en este microservicio y cómo se implementa?
- **comoSeUsa**: ¿Cómo se utiliza un circuit breaker para manejar fallos temporales del servicio externo?
- **erroresComunes**: ¿Cuáles son los errores comunes al implementar un microservicio y cómo se pueden evitar?
- **queDecisionesImplica**: ¿Qué decisiones implica la optimización del rendimiento del microservicio y cómo se justifican?

## Criterios de Evaluacion

- Implementación correcta de la lógica de negocio del microservicio.
- Manejo adecuado de la idempotencia en las solicitudes repetidas.
- Uso efectivo de circuit breakers para manejar fallos temporales del servicio externo.
- Cumplimiento de los umbrales de rendimiento y disponibilidad establecidos.
- Pruebas exhaustivas que garanticen el funcionamiento correcto del microservicio.

## Como trabajar con un asistente de IA

Hay dos caminos, elegi uno:

- **AGENTS.md** (recomendado) — instrucciones nativas del repo. Abri esta carpeta con tu agente local (Claude Code, Cursor, Codex, Copilot, Gemini) y las carga solo. Sabe que archivos faltan y con que comando se verifica, y completa el scaffold escribiendo en disco.
- **PROMPT_MEJORA.md** — para copiar y pegar en un chat (claude.ai, ChatGPT). Devuelve un ZIP con el proyecto. Sirve si no tenes un agente en el IDE.

Ninguno de los dos resuelve las fases del reto: eso es tu trabajo.

## Verificacion

El proyecto esta listo para trabajar cuando este comando corre sin errores:

```bash
el comando de build o arranque canonico del stack elegido
```

---

*Reto generado automaticamente por Challenge Generator - Pragma*
