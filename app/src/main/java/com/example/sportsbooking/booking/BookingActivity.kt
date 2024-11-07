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

        // Initialize RecyclerView and Adapter
        bookingAdapter = BookingAdapter()
        bookingRecyclerView = findViewById(R.id.recycler_view_booking)
        bookingRecyclerView.layoutManager = LinearLayoutManager(this)
        bookingRecyclerView.adapter = bookingAdapter

        fetchBookings()
    }

    private fun fetchBookings() {
        val bookingRef = db.collection("sports_center")
            .document("badminton")
            .collection("courts")
            .document(courtId)
            .collection("bookings")
            .document(selectedDate)

        bookingRef.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                val bookings = document.data?.mapNotNull { entry ->
                    val bookingData = entry.value as? Map<*, *>
                    val bookingUserId = bookingData?.get("userId") as? String
                    if (bookingUserId == userId) {
                        Booking(
                            venueImageResId = R.drawable.venue_image, // Placeholder image
                            venueName = entry.key,
                            venueAddress = bookingData?.get("venueAddress").toString(),
                            venueSport = "Badminton",
                            bookingStatus = bookingData?.get("bookingStatus").toString(),
                            userId = bookingUserId.toString()
                        )
                    } else {
                        null
                    }
                } ?: emptyList()

                bookingAdapter.submitList(bookings)
            } else {
                Toast.makeText(this, "No bookings found for the selected date", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Failed to fetch bookings: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }
}