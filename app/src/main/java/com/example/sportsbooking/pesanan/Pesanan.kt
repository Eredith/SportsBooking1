package com.example.sportsbooking.pesanan

data class Pesanan(
    val sportsCenter: String = "",
    val court: String = "",
    val bookingDate: String = "",
    val timeSlot: String = "",
    val bookedBy: String = "",
    val status: String = ""
) {
    override fun toString(): String {
        return "Sports Center: $sportsCenter\nCourt: $court\nBooking Date: $bookingDate\nTime Slot: $timeSlot\nBooked By: $bookedBy\nStatus: $status"
    }
}