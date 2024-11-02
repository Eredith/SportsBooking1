package com.example.sportsbooking.booking

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sportsbooking.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class BookingActivity : AppCompatActivity() {

    private lateinit var bookingRecyclerView: RecyclerView
    private lateinit var bookingAdapter: BookingAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var courtId: String
    private lateinit var selectedDate: String
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.booking_page)

        // Ambil courtId dan selectedDate dari Intent
        courtId = intent.getStringExtra("courtId") ?: "default_court_id"
        selectedDate = intent.getStringExtra("selectedDate") ?: "default_date"

        // Initialize Firestore and FirebaseAuth
        db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        userId = auth.currentUser?.uid

        if (userId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Initialize RecyclerView
        bookingRecyclerView = findViewById(R.id.recycler_view_booking)
        bookingRecyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize Adapter with a click listener
        bookingAdapter = BookingAdapter { slot ->
            bookTimeSlot(slot.time)
        }

        bookingRecyclerView.adapter = bookingAdapter

        // Load booking slots from Firestore
        loadBookingSlots()
    }

    private fun loadBookingSlots() {
        // Reference to the specific court and date in Firestore
        val bookingsRef = db.collection("sports_center")
            .document("badminton")
            .collection("courts")
            .document(courtId)
            .collection("bookings")
            .document(selectedDate)

        bookingsRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val bookingSlotList = mutableListOf<BookingSlot>()
                document.data?.forEach { (timeSlot, value) ->
                    val slotData = value as? Map<*, *>
                    val isBooked = slotData?.get("status") == "booked"
                    bookingSlotList.add(
                        BookingSlot(
                            time = timeSlot,
                            price = slotData?.get("price")?.toString() ?: "0",
                            isBooked = isBooked
                        )
                    )
                }
                // Submit the list to the adapter
                bookingAdapter.submitList(bookingSlotList)
            } else {
                Toast.makeText(this, "No booking slots available", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Failed to load booking slots: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun bookTimeSlot(timeSlot: String) {
        val bookingRef = db.collection("sports_center")
            .document("badminton")
            .collection("courts")
            .document(courtId)
            .collection("bookings")
            .document(selectedDate)

        val bookingData = mapOf(
            "booked_by" to userId, // Menggunakan userId dari FirebaseAuth
            "status" to "booked"
        )

        bookingRef.update(timeSlot, bookingData).addOnSuccessListener {
            Toast.makeText(this, "Time slot successfully booked!", Toast.LENGTH_SHORT).show()
            loadBookingSlots() // Refresh booking slots after booking
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Failed to book time slot: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
