package com.genetic.darkphantom.models

data class Command(
    val action: String = "",
    val params: Map<String, Any> = emptyMap(),
    val timestamp: Long = 0
)
