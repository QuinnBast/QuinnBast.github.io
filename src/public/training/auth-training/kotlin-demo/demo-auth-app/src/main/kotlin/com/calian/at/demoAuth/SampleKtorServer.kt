package com.calian.at.demoAuth

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.http.content.resources
import io.ktor.server.http.content.static
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun main() {
    KtorServer().start()
}

class KtorServer {
    fun start() {
        embeddedServer(Netty, port = 9001) {
            install(CallLogging)

            install(ContentNegotiation) {
                json()
            }

            routing {
                // Serve some static files from the lesson1 directory
                static {
                    resources("base")
                }

                route("/api") {
                    get("/") {
                        call.respondText("Welcome to the API")
                    }
                }
            }
        }.start(wait = true)
    }
}