plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "2.2.20"
    id("io.ktor.plugin") version "3.3.1"
    application
}

group = "com.albertiacob91.movieversekmp"
version = "0.1.0"

repositories {
    mavenCentral()
    google()
}

application {
    mainClass.set("com.albertiacob91.movieversekmp.server.ApplicationKt")
}

dependencies {
    implementation("io.ktor:ktor-server-core:3.3.1")
    implementation("io.ktor:ktor-server-netty:3.3.1")
    implementation("io.ktor:ktor-server-content-negotiation:3.3.1")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.3.1")
    implementation("io.ktor:ktor-server-status-pages:3.3.1")
    implementation("ch.qos.logback:logback-classic:1.5.18")

    testImplementation("io.ktor:ktor-server-test-host:3.3.1")
    testImplementation(kotlin("test"))

    implementation("org.jetbrains.exposed:exposed-core:0.61.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.61.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.61.0")
    implementation("com.zaxxer:HikariCP:6.3.0")
    implementation("org.postgresql:postgresql:42.7.7")

    implementation("org.jetbrains.exposed:exposed-java-time:0.61.0")

    implementation("io.ktor:ktor-client-core:3.3.1")
    implementation("io.ktor:ktor-client-cio:3.3.1")
    implementation("io.ktor:ktor-client-content-negotiation:3.3.1")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.3.1")
}