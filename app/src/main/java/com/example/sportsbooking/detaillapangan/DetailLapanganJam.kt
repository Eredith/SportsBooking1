package com.example.sportsbooking.detaillapangan

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sportsbooking.R
import com.example.sportsbooking.booking.BookingAdapter
import com.example.sportsbooking.booking.BookingSlot
import com.example.sportsbooking.detailpembayaran.DetailPembayaranActivity
import com.google.firebase.firestore.FirebaseFirestore

class DetailLapanganJam : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: BookingAdapter
    private var selectedSlot: String? = null
    private val courtId = "lapangan_1" // Set the court ID dynamically if needed

    private var venueName: String? = null
    private var venuePrice: String? = null
    private var venueLocation: String? = null
    private var venueCategory: String? = null
    private var venueCapacity: String? = null
    private var venueStatus: String? = null
    private var venueImageUrl: String? = null
    private var venueAvailableStartTime: String? = null
    private var venueAvailableEndTime: String? = null
    private var selectedDate: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_lapangan)

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance()

        // Initialize views
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        val venueImage: ImageView = findViewById(R.id.venueImage)
        val venueTitle: TextView = findViewById(R.id.venueTitle)
        val venueAlamat: TextView = findViewById(R.id.venueAlamat)
        val priceTextView: TextView = findViewById(R.id.total_price_value)
        val selectedDateTextView: TextView = findViewById(R.id.selectedDateTextView)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val bookButton: Button = findViewById(R.id.payButton)

        // Get data from Intent
        intent?.let {
            venueName = it.getStringExtra("venue_name")
            venuePrice = it.getDoubleExtra("venue_price", 0.0).toString()
            venueLocation = it.getStringExtra("venue_location")
            venueCategory = it.getStringExtra("venue_category")
            venueCapacity = it.getStringExtra("venue_capacity")
            venueStatus = it.getStringExtra("venue_status")
            venueImageUrl = it.getStringExtra("venue_imageUrl")
            venueAvailableStartTime = it.getStringExtra("venue_availableStartTime")
            venueAvailableEndTime = it.getStringExtra("venue_availableEndTime")
            selectedDate = it.getStringExtra("selected_date")
        }

        // Set data to views
        toolbar.title = "Detail Lapangan"
        venueTitle.text = venueName
        venueAlamat.text = venueLocation
        priceTextView.text = "Rp${venuePrice}"
        selectedDateTextView.text = "Selected Date: $selectedDate"
        Glide.with(this).load(venueImageUrl).into(venueImage)

        // Set up RecyclerView with GridLayoutManager
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        adapter = BookingAdapter { slot ->
            onSlotSelected(slot)
        }
        recyclerView.adapter = adapter

        // Load booking slots
        loadBookingSlots()

        // Book button click listener
        bookButton.setOnClickListener {
            bookSelectedSlot()
        }
    }

    private fun loadBookingSlots() {
        // Fetch booking slots for the specific court from Firestore
        db.collection("booking_slots")
            .whereEqualTo("courtId", courtId)
            .get()
            .addOnSuccessListener { documents ->
                val slots = documents.map { doc ->
                    BookingSlot(
                        time = doc.getString("time") ?: "",
                        price = doc.getString("price") ?: "",
                        isBooked = doc.getBoolean("isBooked") ?: false
                    )
                }
                adapter.submitList(slots) // Ensure `slots` is a List<BookingSlot>
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to load booking slots: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun onSlotSelected(slot: BookingSlot) {
        selectedSlot = slot.time
        // Update UI to indicate selected slot if needed
    }

    private fun bookSelectedSlot() {
        if (selectedSlot == null) {
            Toast.makeText(this, "Please select a time slot", Toast.LENGTH_SHORT).show()
            return
        }

        // Book the selected slot by adding to Firebase
        db.collection("bookings")
            .add(mapOf(
                "courtId" to courtId,
                "time" to selectedSlot,
                "userId" to "current_user_id" // Replace with actual user ID
            ))
            .addOnSuccessListener { documentReference ->
                // Update the booking slot to mark it as booked
                db.collection("booking_slots")
                    .whereEqualTo("courtId", courtId)
                    .whereEqualTo("time", selectedSlot)
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            db.collection("booking_slots").document(document.id)
                                .update("isBooked", true)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Booking confirmed and slot updated", Toast.LENGTH_SHORT).show()

                                    // Navigate to DetailPembayaranActivity with booking details
                                    val intent = Intent(this, DetailPembayaranActivity::class.java).apply {
                                        putExtra("venue_name", venueName)
                                        putExtra("venue_price", venuePrice)
                                        putExtra("venue_location", venueLocation)
                                        putExtra("venue_category", venueCategory)
                                        putExtra("venue_capacity", venueCapacity)
                                        putExtra("venue_status", venueStatus)
                                        putExtra("venue_imageUrl", venueImageUrl)
                                        putExtra("venue_availableStartTime", venueAvailableStartTime)
                                        putExtra("venue_availableEndTime", venueAvailableEndTime)
                                        putExtra("selected_date", selectedDate)
                                        putExtra("courtId", courtId)
                                        putExtra("time", selectedSlot)
                                        putExtra("bookingId", documentReference.id)
                                    }
                                    startActivity(intent)
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this, "Failed to update slot: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to find slot: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Booking failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}