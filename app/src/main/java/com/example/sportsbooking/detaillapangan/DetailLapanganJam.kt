package com.example.sportsbooking.detaillapangan

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

        // Get selected date from Intent
        val selectedDate = intent.getStringExtra("selected_date")
        selectedDateTextView.text = "Selected Date: $selectedDate"

        // Set up RecyclerView with GridLayoutManager
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        adapter = BookingAdapter { slot ->
            onSlotSelected(slot)
        }
        recyclerView.adapter = adapter

        // Load court details and booking slots
        loadCourtDetails(courtNameTextView, courtTypeTextView, floorTypeTextView)
        loadBookingSlots()

        // Book button click listener
        bookButton.setOnClickListener {
            bookSelectedSlot()
        }
    }

    private fun loadCourtDetails(
        courtNameTextView: TextView,
        courtTypeTextView: TextView,
        floorTypeTextView: TextView
    ) {
        // Fetch court details from Firestore
        db.collection("courts")
            .document(courtId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val courtName = document.getString("name") ?: "Unknown Court"
                    val courtType = document.getString("type") ?: "Unknown Type"
                    val floorType = document.getString("floorType") ?: "Unknown Floor Type"

                    courtNameTextView.text = courtName
                    courtTypeTextView.text = courtType
                    floorTypeTextView.text = floorType
                } else {
                    Toast.makeText(this, "Court details not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to load court details: ${e.message}", Toast.LENGTH_SHORT).show()
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
            .addOnSuccessListener {
                Toast.makeText(this, "Booking confirmed", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Booking failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}