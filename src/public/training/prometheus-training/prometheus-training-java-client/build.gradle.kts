plugins {
    id("java")
}

group = "com.calian.at"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")

    // Prometheus
    implementation("io.prometheus:simpleclient:0.16.0")     // Prometheus metric lib
    implementation("io.prometheus:simpleclient_hotspot:0.16.0")  // Prometheus JVM metrics
    implementation("io.prometheus:simpleclient_httpserver:0.16.0") // Prometheus Server
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
