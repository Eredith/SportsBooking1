package com.example.sportsbooking

import java.util.Date

data class Venue(
    val name: String = "",
    val price: String = "",
    val location: String = "",
    val category: String = "",
    val capacity: Int = 0,
    val imageResource: String = "",
    val status: String = "",
    var availableStartTime: Date? = null, // Ubah dari Calendar? ke Date?
    var availableEndTime: Date? = null    // Ubah dari Calendar? ke Date?
)
