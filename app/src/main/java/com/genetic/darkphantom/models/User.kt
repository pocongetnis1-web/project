package com.genetic.darkphantom.models

data class User(
    val username: String = "",
    val passwordHash: String = "",
    val salt: String = "",
    val role: String = "USER",
    val created_at: Long = 0
)
