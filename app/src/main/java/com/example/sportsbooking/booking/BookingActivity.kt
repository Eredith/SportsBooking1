package com.example.sportsbooking.booking

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sportsbooking.R
import com.google.android.material.tabs.TabLayout

class BookingActivity : AppCompatActivity() {

    // UI Elements
    private lateinit var toolbar: Toolbar
    private lateinit var tabLayout: TabLayout
    private lateinit var searchCard: CardView
    private lateinit var searchBooking: EditText
    private lateinit var filterIcon: ImageView
    private lateinit var recyclerViewBooking: RecyclerView

    // Data Variables
    private var venueName: String? = null
    private var venueCategory: String? = null
    private var venuePrice: String? = null
    private var venueLocation: String? = null
    private var venueCapacity: String? = null
    private var venueStatus: String? = null
    private var venueImageUrl: String? = null
    private var venueAvailableStartTime: String? = null
    private var venueAvailableEndTime: String? = null
    private var bookingDate: String? = null
    private var bookingTime: String? = null
    private var courtId: String? = null
    private var selectedSlot: String? = null
    private var bookingId: String? = null
    private var bookingStatus: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.booking_page)

        // Initialize UI elements
        toolbar = findViewById(R.id.toolbar_booking)
        tabLayout = findViewById(R.id.tab_layout)
        searchCard = findViewById(R.id.search_card)
        searchBooking = findViewById(R.id.search_booking)
        filterIcon = findViewById(R.id.filter_icon)
        recyclerViewBooking = findViewById(R.id.recycler_view_booking)

        // Setup Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        // Get data from Intent
        intent?.let {
            venueName = it.getStringExtra("venue_name")
            venueCategory = it.getStringExtra("venue_category")
            venuePrice = it.getStringExtra("venue_price")
            venueLocation = it.getStringExtra("venue_location")
            venueCapacity = it.getStringExtra("venue_capacity")
            venueStatus = it.getStringExtra("venue_status")
            venueImageUrl = it.getStringExtra("venue_imageUrl")
            venueAvailableStartTime = it.getStringExtra("venue_availableStartTime")
            venueAvailableEndTime = it.getStringExtra("venue_availableEndTime")
            bookingDate = it.getStringExtra("booking_date")
            bookingTime = it.getStringExtra("booking_time")
            courtId = it.getStringExtra("courtId")
            selectedSlot = it.getStringExtra("time")
            bookingId = it.getStringExtra("bookingId")
            bookingStatus = it.getStringExtra("booking_status")
        }

        // Populate UI with data (if needed)
        searchBooking.setText(venueName)

        // Example: searchBooking.setText(venueName)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}