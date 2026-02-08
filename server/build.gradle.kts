import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.10"
    kotlin("plugin.serialization") version "1.9.24"
    application
}

group = "bast.quinn"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

sourceSets {
    main {
        kotlin.srcDirs("src/main/kotlin")
    }
    test {
        kotlin.srcDirs("src/test/kotlin")
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("io.ktor:ktor-server-core:2.3.11")
    implementation("io.ktor:ktor-server-netty:2.3.11")
    implementation("io.ktor:ktor-server-cors:2.3.11")
    implementation("io.ktor:ktor-server-call-logging:2.3.11")
    implementation("io.ktor:ktor-server-content-negotiation:2.3.11")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.11")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.3.2")
    implementation("org.apache.logging.log4j:log4j-api:2.25.3")
    implementation("org.apache.logging.log4j:log4j-core:2.25.3")
    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.23.0")
    implementation("org.slf4j:slf4j-api:2.0.17")
    implementation("com.sksamuel.hoplite:hoplite-core:2.7.5")
    implementation("com.sksamuel.hoplite:hoplite-yaml:2.7.5")
    implementation("io.ktor:ktor-server-metrics-micrometer:2.3.11")
    implementation("io.micrometer:micrometer-registry-prometheus:1.10.3")
    implementation("org.yaml:snakeyaml:2.2")
}

tasks.withType<KotlinCompile>().all {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_19.toString()
    }
}

application {
    mainClass.set("bast.quinn.finance.PersonalServerMainKt")
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to application.mainClass.get()
        )
    }
}