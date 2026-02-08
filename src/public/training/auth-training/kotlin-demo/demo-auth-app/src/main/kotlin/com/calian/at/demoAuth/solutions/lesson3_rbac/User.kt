package com.calian.at.demoAuth.solutions.lesson3_rbac

import io.ktor.server.auth.Principal
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val username: String,
    val password: String,
    val roles: List<Role> = listOf()
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