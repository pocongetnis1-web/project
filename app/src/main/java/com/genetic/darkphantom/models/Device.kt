package com.genetic.darkphantom.models

data class Device(
    var id: String = "",
    val model: String = "",
    val brand: String = "",
    val sdk: Int = 0,
    val battery: Int = 0,
    val online: Boolean = false,
    val last_seen: Long = 0
)
