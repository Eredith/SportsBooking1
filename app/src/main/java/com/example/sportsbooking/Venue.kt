package com.example.sportsbooking

import java.util.Calendar

data class Venue(
    val name: String,
    val price: String,
    val location: String,
    val category: String,
    val capacity: Int,
    val imageResId: Int,
    var availableStartTime: Calendar?, // Waktu mulai ketersediaan
    var availableEndTime: Calendar?    // Waktu selesai ketersediaan
)