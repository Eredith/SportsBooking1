package com.example.sportsbooking

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class BookingActivity : AppCompatActivity() {

    private lateinit var bookingRecyclerView: RecyclerView
    private lateinit var bookingAdapter: BookingAdapter
    private lateinit var bookingSlotList: List<BookingSlot>
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.booking_page)

        // Initialize Firestore
        db = FirebaseFirestore.getInstance()

        // Initialize RecyclerView
        bookingRecyclerView = findViewById(R.id.recycler_view_booking)
        bookingRecyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize Adapter with a click listener
        bookingAdapter = BookingAdapter { slot ->
            // Handle booking slot click
            // For example, navigate to a details page or show a Toast
            Toast.makeText(this, "Selected Slot: ${slot.time}", Toast.LENGTH_SHORT).show()
        }

        bookingRecyclerView.adapter = bookingAdapter

        // Load booking slots from Firestore
        loadBookingSlots()
    }

    private fun loadBookingSlots() {
        // Fetch booking slots from Firestore
        db.collection("booking_slots")
            .get()
            .addOnSuccessListener { documents ->
                bookingSlotList = documents.map { doc ->
                    BookingSlot(
                        time = doc.getString("time") ?: "Unavailable",
                        price = doc.getString("price") ?: "0",
                        isBooked = doc.getBoolean("isBooked") ?: false
                    )
                }
                // Submit the list to the adapter
                bookingAdapter.submitList(bookingSlotList)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to load booking slots: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
