// Booking.kt
package com.example.sportsbooking.booking

data class Booking(
    val venueImageResId: Int,   // Resource ID untuk gambar venue
    val venueName: String,      // Nama venue
    val venueAddress: String,   // Alamat venue
    val venueSport: String,     // Jenis olahraga
    val bookingStatus: String   // Status pemesanan
)
