package com.example.sportsbooking.detaillapangan

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_lapangan)

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance()

        // Initialize views
        val courtNameTextView: TextView = findViewById(R.id.courtNameTextView)
        val courtTypeTextView: TextView = findViewById(R.id.courtTypeTextView)
        val floorTypeTextView: TextView = findViewById(R.id.floorTypeTextView)
        val selectedDateTextView: TextView = findViewById(R.id.selectedDateTextView)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val bookButton: Button = findViewById(R.id.bookButton)

        // Get data from Intent
        val courtName = intent.getStringExtra("venue_name") ?: "Unknown Court"
        val courtType = intent.getStringExtra("venue_category") ?: "Unknown Type"
        val floorType = intent.getStringExtra("venue_status") ?: "Unknown Floor Type"
        val selectedDate = intent.getStringExtra("selected_date")

        // Set data to TextViews
        courtNameTextView.text = courtName
        courtTypeTextView.text = courtType
        floorTypeTextView.text = floorType
        selectedDateTextView.text = "Selected Date: $selectedDate"

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

    private fun uploadBookingSlots() {
        val startHour = 10
        val endHour = 22

        for (hour in startHour until endHour) {
            val timeSlot = if (hour < 12) {
                "$hour:00 AM"
            } else {
                val adjustedHour = if (hour == 12) 12 else hour - 12
                "$adjustedHour:00 PM"
            }

            val newBookingSlot = hashMapOf(
                "courtId" to courtId,
                "time" to timeSlot,
                "price" to "100",
                "isBooked" to false
            )

            // Add the new booking slot to Firestore
            db.collection("booking_slots")
                .add(newBookingSlot)
                .addOnSuccessListener { documentReference ->
                    Toast.makeText(this, "Booking slot added with ID: ${documentReference.id}", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error adding booking slot: ${e.message}", Toast.LENGTH_SHORT).show()
                }
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