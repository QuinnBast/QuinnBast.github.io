package bast.quinn.finance

import bast.quinn.finance.PersonalServerMain.Companion.appMicrometerRegistry
import bast.quinn.finance.config.ServerConfig
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Application.routing(config: ServerConfig) {
    routing {
        singlePageApplication {
            vue(config.pathToDist)
        }
        get("/metrics") {
            call.respond(appMicrometerRegistry.scrape())
        }
    }
}