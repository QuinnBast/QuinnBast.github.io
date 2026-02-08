package com.calian.at.demoAuth.solutions.lesson9_mfa

import io.ktor.server.auth.Principal
import io.ktor.server.auth.UserIdPrincipal
import kotlinx.serialization.Serializable
import org.keycloak.TokenVerifier
import org.keycloak.representations.AccessToken

@Serializable
data class User(
    val username: String,
    val salt: String,
    val hashedPassword: String,
    val optSecret: String,
    val roles: List<Role> = listOf()
)

@Serializable
data class LoginRequest(
    val username: String,
    val password: String,
    val otpGuess: String,
)

@Serializable
data class UserRequest(
    val username: String,
    val password: String,
    val roles: List<Role>
)

@Serializable
data class UserSession(
    val user: User,
    val sessionId: String,
    val timeCreated: Long,
    val expiresAt: Long
) : Principal

data class LdapUserSession(
    val username: String,
    val email: String,
    val roles: List<String>,
    val expiresAt: Long,
) : Principal

enum class Role {
    ADMIN,
    USER,
    VIEWER
}

@Serializable
data class OAuthConfiguration(
    val name: String = "keycloak",
    val clientId: String = "oidc-client",
    val secret: String = "dBQ9k4W2oAZ4gimxTKA4a8pfPeyceU1t",
    val authUrl: String = "http://localhost:8080/realms/master/protocol/openid-connect/auth",
    val tokenUrl: String = "http://localhost:8080/realms/master/protocol/openid-connect/token",
    val userInfoUrl: String = "http://localhost:8080/realms/master/protocol/openid-connect/userinfo",
    val scopes: List<String> = listOf("openid", "profile", "email", "roles"),
)

data class OauthSession(
    val accessToken: String,
    val refreshToken: String?,
    val expiresIn: Long,
) : Principal {
    fun userSession(): AccessToken? {
        return TokenVerifier.create(accessToken, AccessToken::class.java).token
    }
}