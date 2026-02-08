package com.calian.at.demoAuth.solutions.lesson2_sessions

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val username: String,
    val password: String,
)

data class UserSession(
    val user: User,
    val sessionId: String,
    val timeCreated: Long,
    val expiresAt: Long
)