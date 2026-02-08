package com.calian.at.demoAuth.solutions.lesson7_ldap

import io.ktor.server.auth.Principal
import io.ktor.server.auth.UserIdPrincipal
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val username: String,
    val salt: String,
    val hashedPassword: String,
    val roles: List<Role> = listOf()
)

@Serializable
data class LoginRequest(
    val username: String,
    val password: String
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