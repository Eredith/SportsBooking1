package com.example.sportsbooking.days

data class Day(
    val name: String,    // e.g., "Mon"
    val date: Int,       // e.g., 25
    val month: String,   // e.g., "Januari"
    var selected: Boolean = false,
    val isAvailable: Boolean = true // Optional availability status
)
