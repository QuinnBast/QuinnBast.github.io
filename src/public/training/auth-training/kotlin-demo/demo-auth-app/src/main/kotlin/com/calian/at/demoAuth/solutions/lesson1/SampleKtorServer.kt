package com.calian.at.demoAuth.solutions.lesson1

import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.http.content.resources
import io.ktor.server.http.content.static
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
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

            install(CORS) {
                allowSameOrigin = true
            }

            val userDatabase = UserDatabase()

            routing {
                // Serve some static files from the lesson1 directory
                static("/") {
                    resources("lesson1")
                }

                route("/api") {
                    post("/login") {
                        val login = call.receive<User>()
                        val user = userDatabase.getUser(login.username)

                        if (user != null && user.password == login.password) {
                            call.respond(HttpStatusCode.OK, "Login successful")
                        } else {
                            call.respond(HttpStatusCode.Unauthorized, "Invalid username or password")
                        }
                    }
                }
            }
        }.start(wait = true)
    }
}