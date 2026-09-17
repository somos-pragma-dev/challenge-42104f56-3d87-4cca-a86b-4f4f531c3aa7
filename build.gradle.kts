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