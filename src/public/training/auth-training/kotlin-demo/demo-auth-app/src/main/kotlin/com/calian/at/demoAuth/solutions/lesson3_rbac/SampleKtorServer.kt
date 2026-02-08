package com.calian.at.demoAuth.solutions.lesson3_rbac

import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.session
import io.ktor.server.engine.embeddedServer
import io.ktor.server.http.content.resources
import io.ktor.server.http.content.static
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.sessions.SessionTransportTransformerEncrypt
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.clear
import io.ktor.server.sessions.cookie
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
import io.ktor.util.hex
import java.util.UUID

fun main() {
    KtorServer().start()
}

class KtorServer {

    fun start() {
        embeddedServer(Netty, port = 9001) {
            install(CallLogging)

            install(Sessions) {
                val secretEncryptKey = hex("00112233445566778899aabbccddeeff")
                val secretSignKey = hex("6819b57a326945c1968f45236589")
                cookie<UserSession>("Auth-Demo-Cookie") {
                    cookie.maxAgeInSeconds = 60
                    transform(SessionTransportTransformerEncrypt(secretEncryptKey, secretSignKey))
                }
            }

            install(Authentication) {
                session<UserSession>("session_login") {
                    // If someone fails to validate, this is what happens.
                    challenge {
                        // Typically you might want to redirect them to a login page.
                        call.respond(HttpStatusCode.Unauthorized, "Not authenticated")
                    }
                    // This is the function used to "verify" if the user is logged in.
                    validate { session ->
                        if (session != null) {
                            session
                        } else {
                            null
                        }
                    }
                }
                session<UserSession>("is_admin") {
                    challenge {
                        call.respond(HttpStatusCode.Unauthorized, "Not authenticated")
                    }
                    validate { session ->
                        if (session != null && session.user.roles.contains(Role.ADMIN)) {
                            session
                        } else {
                            null
                        }
                    }
                }
            }

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
                    resources("lesson3")
                }

                route("/api") {
                    post("/login") {
                        val login = call.receive<User>()
                        val user = userDatabase.getUser(login.username)

                        if (user != null && user.password == login.password) {
                            val session = UserSession(
                                user,
                                UUID.randomUUID().toString(),
                                System.currentTimeMillis(),
                                System.currentTimeMillis() + 1000 * 60 * 60
                            )

                            call.sessions.set<UserSession>(session)
                            call.respond(HttpStatusCode.OK, "Login successful")
                        } else {
                            call.respond(HttpStatusCode.Unauthorized, "Invalid username or password")
                        }
                    }
                    authenticate("session_login") {
                        get("/session") {
                            val session = call.sessions.get<UserSession>()
                            call.respond(HttpStatusCode.OK, session.toString())
                        }
                        get("/logout") {
                            call.sessions.clear<UserSession>()
                            call.respond(HttpStatusCode.OK, "Logged out")
                        }
                    }
                    authenticate("is_admin") {
                        get("/admin") {
                            call.respond(HttpStatusCode.OK, "You are an admin!")
                        }
                    }
                }
            }
        }.start(wait = true)
    }
}