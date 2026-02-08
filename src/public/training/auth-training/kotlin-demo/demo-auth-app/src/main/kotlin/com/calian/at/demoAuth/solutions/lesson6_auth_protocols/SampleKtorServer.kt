package com.calian.at.demoAuth.solutions.lesson6_auth_protocols

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.basic
import io.ktor.server.auth.bearer
import io.ktor.server.auth.digest
import io.ktor.server.auth.form
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.auth.session
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.engine.sslConnector
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
import io.ktor.util.toCharArray
import java.io.File
import java.security.KeyStore
import java.security.MessageDigest
import java.util.Base64
import java.util.Date
import java.util.UUID
import kotlin.text.Charsets.UTF_8
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

fun main() {
    KtorServer().start()
}

class KtorServer {
    fun start() {
        val keystoreFile = File("server.jks")
        val keyStore: KeyStore = KeyStore.getInstance(KeyStore.getDefaultType())
        val keystorePassword = "123456"
        keyStore.load(keystoreFile.inputStream(), keystorePassword.toCharArray())

        val environment = applicationEngineEnvironment {
            // Default port
            connector {
                this.port = 9001
            }
            // SSL port
            sslConnector(
                keyStore,
                keyAlias = "auth-demo",
                { keystorePassword.toCharArray() }, // Keystore password
                { keystorePassword.toCharArray() }  // Private key password
            ) {
                port = 9443
                keyStorePath = keystoreFile
            }
            module(Application::module)
        }

        embeddedServer(Netty, environment).start(wait = true)
    }
}

fun Application.module() {
    val userDatabase = UserDatabase()
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
        basic("basic") {
            realm = "Basic Auth"
            validate { credentials ->
                val user = userDatabase.getUser(credentials.name)

                if (user != null && userDatabase.verifyUser(credentials.name, credentials.password)) {
                    UserSession(
                        user,
                        UUID.randomUUID().toString(),
                        System.currentTimeMillis(),
                        System.currentTimeMillis() + 1000 * 60 * 60
                    )
                } else {
                    null
                }
            }
        }
        bearer("bearer") {
            realm = "Bearer Auth"
            authenticate { tokenCredential ->
                val decoded = Base64.getDecoder().decode(tokenCredential.token.toByteArray())
                val userSession = Json.decodeFromString<UserSession>(String(decoded))
                if(userSession != null) {
                    userSession
                } else {
                    null
                }
            }
        }
        form("form") {
            userParamName = "username"
            passwordParamName = "password"
            validate { credentials ->
                val user = userDatabase.getUser(credentials.name)

                if (user != null && userDatabase.verifyUser(credentials.name, credentials.password)) {
                    UserSession(
                        user,
                        UUID.randomUUID().toString(),
                        System.currentTimeMillis(),
                        System.currentTimeMillis() + 1000 * 60 * 60
                    )
                } else {
                    null
                }
            }
            challenge {
                call.respond(HttpStatusCode.Unauthorized, "Credentials are not valid")
            }
        }
        digest("digest") {
            realm = "Digest Realm"
            digestProvider { userName, realm ->
                // Needs to respond with an MD5 hash of the `HA1` value, ie, `md5(username:realm:password)`
                val md5Encoder = MessageDigest.getInstance("MD5")
                md5Encoder.digest("$userName:$realm:Password".toByteArray(UTF_8))
            }
            validate { credentials ->
                // The digest provider will provide the `HA1` value, so we just need to check if the user exists after being validated.
                val user = userDatabase.getUser(credentials.userName)
                if (user != null) {
                    UserSession(
                        userDatabase.getUser(credentials.userName)!!,
                        UUID.randomUUID().toString(),
                        System.currentTimeMillis(),
                        System.currentTimeMillis() + 1000 * 60 * 60
                    )
                } else {
                    null
                }
            }
        }
        jwt("jwt") {
            realm = "Jwt Auth"
            // Ensure that the JWT is signed with the correct algorithm and secret.
            verifier(
                JWT
                .require(Algorithm.HMAC256("1234567890"))
                .withAudience("auth-demo")
                .withIssuer("0.0.0.0")
                .build()
            )
            // Validate the JWT payload
            validate { credential ->
                // Here, since we are only generating tokens after valid logins, we will just check if the token is expired or not
                val username = credential.payload.getClaim("username").asString()!!
                if (credential.expiresAt!!.after(Date())) {
                    println("Validated JWT token. User: $username")
                    UserSession(
                        userDatabase.getUser(username)!!,
                        UUID.randomUUID().toString(),
                        System.currentTimeMillis(),
                        System.currentTimeMillis() + 1000 * 60 * 60
                    )
                } else {
                    null
                }
            }
            // Finally, if the JWT is invalid, we will respond with a 401 Unauthorized
            challenge { defaultScheme, realm ->
                call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
            }
        }
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

    routing {
        // Serve some static files from the lesson1 directory
        static("/") {
            resources("lesson6")
        }

        route("/api") {
            post("/login") {
                val login = call.receive<LoginRequest>()
                val user = userDatabase.getUser(login.username)

                if (user != null && userDatabase.verifyUser(login.username, login.password)) {
                    val token = JWT.create()
                        .withAudience("auth-demo")
                        .withIssuer("0.0.0.0")
                        .withClaim("username", user.username)
                        .withExpiresAt(Date(System.currentTimeMillis() + 60000))
                        .sign(Algorithm.HMAC256("1234567890"))

                    call.respond(HttpStatusCode.OK, hashMapOf("token" to token))
                } else {
                    call.respond(HttpStatusCode.Unauthorized, "Invalid username or password")
                }
            }
            authenticate("bearer") {
                get("/bearer") {
                    call.respond(HttpStatusCode.OK, "Authenticated through Bearer auth!")
                }
            }
            authenticate("form") {
                post("/form") {
                    call.respond(HttpStatusCode.OK, "Authenticated through Form-based auth!")
                }
            }
            authenticate("basic") {
                get("/basic") {
                    call.respond(HttpStatusCode.OK, "Authenticated through Basic auth!")
                }
            }
            authenticate("digest") {
                get("/digest") {
                    call.respond(HttpStatusCode.OK, "Authenticated through Digest auth!")
                }
            }
            authenticate("jwt") {
                get("/jwt") {
                    call.respond(HttpStatusCode.OK, "Authenticated through JWT auth!")
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
                get("/users") {
                    call.respond(HttpStatusCode.OK, userDatabase.users.values)
                }
                post("/user") {
                    val userRequest = call.receive<UserRequest>()
                    val existingUser = userDatabase.getUser(userRequest.username)
                    if (existingUser != null) {
                        // Update an existing user's roles
                        userDatabase.users[userRequest.username] = userDatabase.users[userRequest.username]!!
                            .copy(roles = userRequest.roles)
                        call.respond(HttpStatusCode.OK, "Updated user roles")
                    } else {
                        userDatabase.addUser(userRequest.username, userRequest.password, userRequest.roles)
                        call.respond(HttpStatusCode.OK, "User added")
                    }
                }
            }
        }
    }
}