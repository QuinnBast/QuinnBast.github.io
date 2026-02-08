package com.calian.at.demoAuth.solutions.lesson1

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val username: String,
    val password: String,
)