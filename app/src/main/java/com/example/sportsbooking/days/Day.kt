package com.example.sportsbooking.days

data class Day(
    val name: String,    // e.g., "Mon"
    val date: Int,       // e.g., 25
    val month: String,   // e.g., "Januari"
    val isAvailable: Boolean = true // Optional availability status
)
