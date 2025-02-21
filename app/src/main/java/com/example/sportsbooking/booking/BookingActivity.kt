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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


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
    private val activeBookings = mutableListOf<Booking>()
    private val historyBookings = mutableListOf<Booking>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.booking_page)

        // Initialize UI elements first
        toolbar = findViewById(R.id.toolbar_booking)
        tabLayout = findViewById(R.id.tab_layout)
        searchCard = findViewById(R.id.search_card)
        searchBooking = findViewById(R.id.search_booking)
        filterIcon = findViewById(R.id.filter_icon)
        recyclerViewBooking = findViewById(R.id.recyclerViewBookings)

        db = FirebaseFirestore.getInstance()

        // Now that tabLayout is initialized, add the listener
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> displayBookings(activeBookings)
                    1 -> displayBookings(historyBookings)
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // Load user data and bookings after initializing UI
        loadUserData()

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            fetchUserBookingsBadminton(userId)
            fetchUserBookingsDriving(userId)
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }

        // Setup Toolbar and Bottom Navigation
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        recyclerViewBooking.layoutManager = LinearLayoutManager(this)
        recyclerViewBooking.adapter = BookingAdapter(emptyList())
        setupBottomNavigation()
    }


    private fun loadUserData() {
        val user = FirebaseAuth.getInstance().currentUser
        val profileImageView = findViewById<ImageView>(R.id.profileImageNavbar)

        user?.let {
            db.collection("users").document(user.uid).get()
                .addOnSuccessListener { document ->
                    val profileImageUrl = document.getString("profileImageUrl")
                    if (!profileImageUrl.isNullOrEmpty()) {
                        Glide.with(this)
                            .load(profileImageUrl)
                            .circleCrop()
                            .placeholder(R.drawable.default_profile)
                            .into(profileImageView)
                    } else {
                        profileImageView.setImageResource(R.drawable.default_profile)
                    }
                }
                .addOnFailureListener {
                    profileImageView.setImageResource(R.drawable.default_profile)
                }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setupBottomNavigation() {
        findViewById<LinearLayout>(R.id.nav_home).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

        }

        findViewById<LinearLayout>(R.id.nav_venue).setOnClickListener {
            val intent = Intent(this, VenueListActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

        }

        findViewById<LinearLayout>(R.id.nav_history).setOnClickListener {

        }

        findViewById<LinearLayout>(R.id.nav_profile).setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        }

        findViewById<LinearLayout>(R.id.nav_makanan).setOnClickListener {
            val intent = Intent(this, MakananActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

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
        getUsername(userId) { username ->
            val currentTime = Calendar.getInstance().time

            for (category in categories) {
                val courtsRef = db.collection("sports_center")
                    .document(category)
                    .collection("courts")

                courtsRef.get()
                    .addOnSuccessListener { courtDocuments ->
                        for (courtDocument in courtDocuments) {
                            val court = courtDocument.id
                            val bookingsRef = db.collection("sports_center")
                                .document(category)
                                .collection("courts")
                                .document(court)
                                .collection("bookings")

                            bookingsRef.get()
                                .addOnSuccessListener { bookingDocuments ->
                                    for (bookingDocument in bookingDocuments) {
                                        val bookingDate = bookingDocument.id
                                        val bookingData = bookingDocument.data

                                        bookingData.forEach { (timeSlot, details) ->
                                            if (details is Map<*, *>) {
                                                val bookedBy = details["booked_by"] as? String
                                                val status = details["status"] as? String

                                                if (bookedBy == userId && status == "booked") {
                                                    // Parse the booking time (assumed booking lasts one hour)
                                                    val bookingDateTimeString = "$bookingDate $timeSlot"
                                                    val bookingTime: Date? = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).parse(bookingDateTimeString)

                                                    if (bookingTime != null) {
                                                        // For moving after one hour past booking time
                                                        val calendar = Calendar.getInstance().apply {
                                                            time = bookingTime
                                                            add(Calendar.HOUR_OF_DAY, 1)
                                                        }
                                                        val bookingTimePlusOneHour = calendar.time

                                                        if (bookingTimePlusOneHour.before(currentTime)) {
                                                            // If more than one hour has passed: add to history list
                                                            historyBookings.add(
                                                                Booking(category, court, bookingDate, timeSlot, username, status)
                                                            )
                                                        } else {
                                                            // Otherwise, add to active bookings
                                                            activeBookings.add(
                                                                Booking(category, court, bookingDate, timeSlot, username, status)
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    // Default to showing active bookings when data is loaded
                                    displayBookings(activeBookings)
                                }
                                .addOnFailureListener { e ->
                                    Log.e("BookingActivity", "Failed to fetch bookings for $category - $court", e)
                                }
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("BookingActivity", "Failed to fetch courts for category $category", e)
                    }
            }
        }
    }


    private fun displayBookings(bookings: List<Booking>) {
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

    private fun moveBookingToHistory(category: String, court: String, bookingDate: String, timeSlot: String, userId: String) {
        val bookingRef = db.collection("sports_center")
            .document(category)
            .collection("courts")
            .document(court)
            .collection("bookings")
            .document(bookingDate)

        val historyRef = db.collection("users")
            .document(userId)
            .collection("history")
            .document("$bookingDate-$timeSlot")

        db.runTransaction { transaction ->
            val bookingData = transaction.get(bookingRef).get(timeSlot) as? Map<String, Any>
            if (bookingData != null) {
                transaction.set(historyRef, bookingData) // Move data to history
                transaction.update(bookingRef, timeSlot, null) // Remove from active bookings
            }
        }.addOnSuccessListener {
            Log.d("BookingActivity", "Booking moved to history: $bookingDate $timeSlot")
        }.addOnFailureListener { e ->
            Log.e("BookingActivity", "Failed to move booking to history", e)
        }
    }


    private fun getUsername(userId: String, callback: (String) -> Unit) {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val username = document.getString("username") ?: userId
                    callback(username)
                } else {
                    callback(userId)
                }
            }
            .addOnFailureListener { e ->
                Log.e("BookingActivity", "Failed to fetch username for userId: $userId", e)
                callback(userId)
            }
    }
}
