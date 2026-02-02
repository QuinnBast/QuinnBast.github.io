package bast.quinn.finance

import bast.quinn.finance.config.ServerConfig
import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addFileSource
import com.sksamuel.hoplite.addResourceSource
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.metrics.micrometer.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics
import io.micrometer.core.instrument.binder.system.ProcessorMetrics
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import org.slf4j.LoggerFactory
import java.io.File

class PersonalServerMain {

    companion object {
        val logger = LoggerFactory.getLogger(PersonalServerMain::class.java)
        val appMicrometerRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
    }

    fun run() {
        val config = ConfigLoaderBuilder.default()
            .addFileSource(File("/app/config.yaml"), true)
            .addResourceSource("/server-config.yaml")
            .build()
            .loadConfigOrThrow<ServerConfig>()

        logger.info("Config: {}", config)

        embeddedServer(Netty, host = config.host, port = config.port) {
            install(CORS) {
                anyHost()
                allowHeader(HttpHeaders.ContentType)
                allowMethod(HttpMethod.Put)
                allowMethod(HttpMethod.Delete)
                allowMethod(HttpMethod.Post)
                allowMethod(HttpMethod.Get)
            }
            install(CallLogging)
            install(ContentNegotiation) { json() }
            install(MicrometerMetrics) {
                registry = appMicrometerRegistry
                meterBinders = listOf(
                    JvmMemoryMetrics(),
                    JvmGcMetrics(),
                    ProcessorMetrics()
                )
                timers { call, exception ->
                    // Count each call as well.
                    appMicrometerRegistry.counter("ktor.server.requests",
                        "method", call.request.httpMethod.value,
                        "route", call.request.path(),
                        "status", call.response.status()?.value.toString(),
                    ).increment()
                }
            }
            routing(config)
        }.start(wait = true)
    }
}

fun main() {
    PersonalServerMain().run()
}