package com.calian.at.demoAuth.solutions.lesson5_hashing

import io.ktor.server.auth.Principal
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

data class UserSession(
    val user: User,
    val sessionId: String,
    val timeCreated: Long,
    val expiresAt: Long
) : Principal

enum class Role {
    ADMIN,
    USER,
    VIEWER
}