// BookingActivity.kt
package com.example.sportsbooking.booking

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sportsbooking.R

class BookingActivity : AppCompatActivity() {

    private lateinit var bookingRecyclerView: RecyclerView
    private lateinit var bookingAdapter: BookingAdapter
    private lateinit var bookingList: List<Booking>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.booking_page)

        // Contoh data pesanan (booking)
        bookingList = listOf(
            Booking(
                venueImageResId = R.drawable.ic_venue_placeholder,  // Ganti dengan gambar venue yang sesuai
                venueName = "ASATU ARENA CIKINI",
                venueAddress = "Asatu Area, JL. R....",
                venueSport = "Mini Soccer",
                bookingStatus = "Status Pesanan: Berhasil"
            ),
            Booking(
                venueImageResId = R.drawable.ic_venue_placeholder,
                venueName = "LAPANGAN ABCD",
                venueAddress = "Jl. Contoh No. 123",
                venueSport = "Basketball",
                bookingStatus = "Status Pesanan: Pending"
            )
        )

        // Inisialisasi RecyclerView
        bookingRecyclerView = findViewById(R.id.recycler_view_booking)
        bookingRecyclerView.layoutManager = LinearLayoutManager(this)

        // Inisialisasi Adapter dan hubungkan dengan RecyclerView
        bookingAdapter = BookingAdapter(bookingList)
        bookingRecyclerView.adapter = bookingAdapter
    }
}
