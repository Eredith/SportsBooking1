package com.example.sportsbooking.booking

import Booking
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sportsbooking.MainActivity
import com.example.sportsbooking.R
import com.example.sportsbooking.profile.ProfileActivity
import com.example.sportsbooking.store.MakananActivity
import com.example.sportsbooking.venue.VenueListActivity
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth


class BookingActivity : AppCompatActivity() {

    // UI Elements
    private lateinit var db: FirebaseFirestore
    private lateinit var toolbar: Toolbar
    private lateinit var tabLayout: TabLayout
    private lateinit var searchCard: CardView
    private lateinit var searchBooking: EditText
    private lateinit var filterIcon: ImageView
    private lateinit var recyclerViewBooking: RecyclerView
    private val allBookings = mutableListOf<Booking>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.booking_page)

        db = FirebaseFirestore.getInstance()

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            // Ambil data booking badminton dan driving range secara bersamaan
            fetchUserBookingsBadminton(userId)
            fetchUserBookingsDriving(userId)
        } else {
            // Handle the case where the user is not logged in
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }

        // Initialize UI elements
        toolbar = findViewById(R.id.toolbar_booking)
        tabLayout = findViewById(R.id.tab_layout)
        searchCard = findViewById(R.id.search_card)
        searchBooking = findViewById(R.id.search_booking)
        filterIcon = findViewById(R.id.filter_icon)
        recyclerViewBooking = findViewById(R.id.recyclerViewBookings)

        // Setup Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        // Setup RecyclerView
        recyclerViewBooking.layoutManager = LinearLayoutManager(this)
        recyclerViewBooking.adapter = BookingAdapter(emptyList()) // Set an empty adapter initially

        // Example: searchBooking.setText(venueName)
        // Bottom Navigation
        setupBottomNavigation()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setupBottomNavigation() {
        findViewById<LinearLayout>(R.id.nav_home).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        findViewById<LinearLayout>(R.id.nav_venue).setOnClickListener {
            val intent = Intent(this, VenueListActivity::class.java)
            startActivity(intent)
        }

        findViewById<LinearLayout>(R.id.nav_history).setOnClickListener {

        }

        findViewById<LinearLayout>(R.id.nav_profile).setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        findViewById<LinearLayout>(R.id.nav_makanan).setOnClickListener {
            val intent = Intent(this, MakananActivity::class.java)
            startActivity(intent)
        }
    }

    private fun displayUserBookings(bookings: List<Booking>) {
        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewBookings)
        val emptyView: TextView = findViewById(R.id.empty_view)

        if (bookings.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyView.visibility = View.GONE
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = BookingAdapter(bookings)
        }
    }

    private fun fetchUserBookingsBadminton(userId: String) {
        val categories = listOf("badminton")
        fetchUserBookings(userId, categories) // Panggil fungsi generic
    }

    private fun fetchUserBookingsDriving(userId: String) {
        val categories = listOf("driving range")
        fetchUserBookings(userId, categories) // Panggil fungsi generic
    }

    private fun fetchUserBookings(userId: String, categories: List<String>) {
        // Hapus variabel bookings di sini

        for (category in categories) {
            val courtsRef = db.collection("sports_center")
                .document(category)
                .collection("courts")

            courtsRef.get()
                .addOnSuccessListener { courtDocuments ->
                    if (courtDocuments.isEmpty) {
                        Log.d("BookingActivity", "No courts found for category $category")
                    } else {
                        for (courtDocument in courtDocuments) {
                            val court = courtDocument.id
                            val bookingsRef = db.collection("sports_center")
                                .document(category)
                                .collection("courts")
                                .document(court)
                                .collection("bookings")

                            bookingsRef.get()
                                .addOnSuccessListener { bookingDocuments ->
                                    if (bookingDocuments.isEmpty) {
                                        Log.d("BookingActivity", "No bookings found for $category - $court")
                                    } else {
                                        for (bookingDocument in bookingDocuments) {
                                            val bookingDate = bookingDocument.id
                                            val bookingData = bookingDocument.data

                                            bookingData.forEach { (timeSlot, details) ->
                                                if (details is Map<*, *>) {
                                                    val bookedBy = details["booked_by"] as? String
                                                    val status = details["status"] as? String
                                                    if (bookedBy == userId && status == "booked") {
                                                        // Tambahkan booking ke allBookings
                                                        allBookings.add(
                                                            Booking(
                                                                category,
                                                                court,
                                                                bookingDate,
                                                                timeSlot,
                                                                bookedBy,
                                                                status
                                                            )
                                                        )
                                                        Log.d("BookingActivity", "Found booking at $timeSlot on $bookingDate for $category - $court")
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    // Cek apakah ini iterasi terakhir dari kedua kategori
                                    if (categories.indexOf(category) == categories.size -1 && courtDocument == courtDocuments.last() ) {
                                        displayUserBookings(allBookings)
                                    }
                                }
                                .addOnFailureListener { e ->
                                    Log.e("BookingActivity", "Failed to fetch bookings for $category - $court", e)
                                }
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("BookingActivity", "Failed to fetch courts for category $category", e)
                }
        }
    }
}
