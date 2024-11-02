package com.example.sportsbooking.detailpembayaran

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.example.sportsbooking.booking.BookingActivity
import com.example.sportsbooking.R

class DetailPembayaranActivity : AppCompatActivity() {

    // UI Elements
    private lateinit var toolbar: Toolbar
    private lateinit var venueImage: ImageView
    private lateinit var venueTitle: TextView
    private lateinit var venueSubtitle: TextView
    private lateinit var dateText: TextView
    private lateinit var timeText: TextView
    private lateinit var priceText: TextView
    private lateinit var totalPriceValue: TextView
    private lateinit var termsCheckbox: CheckBox
    private lateinit var payButton: Button

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_pembayaran)

        // Initialize UI elements
        toolbar = findViewById(R.id.toolbar)
        venueImage = findViewById(R.id.venueImage)
        venueTitle = findViewById(R.id.venueTitle)
        venueSubtitle = findViewById(R.id.venueSubtitle)
        dateText = findViewById(R.id.date_text)
        timeText = findViewById(R.id.time_text)
        priceText = findViewById(R.id.price_text)
        totalPriceValue = findViewById(R.id.total_price_value)
        termsCheckbox = findViewById(R.id.terms_checkbox)
        payButton = findViewById(R.id.pay_button)

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
            bookingDate = it.getStringExtra("selected_date")
            bookingTime = it.getStringExtra("time")
            courtId = it.getStringExtra("courtId")
            selectedSlot = it.getStringExtra("time")
            bookingId = it.getStringExtra("bookingId")
        }

        // Populate UI with data
        venueTitle.text = "$venueName\n$venueCategory"
        venueSubtitle.text = venueCategory
        dateText.text = bookingDate ?: "Tanggal belum dipilih"
        timeText.text = bookingTime ?: "Waktu belum dipilih"
        priceText.text = venuePrice ?: "Rp0"
        totalPriceValue.text = venuePrice ?: "Rp0"

        // Load venue image using Glide
        Glide.with(this)
            .load(venueImageUrl)
            .placeholder(R.drawable.venue_image)
            .error(R.drawable.venue_image)
            .centerCrop()
            .into(venueImage)

        // Handle Terms and Conditions
        val termsText: TextView = findViewById(R.id.terms_text)
        val termsLink: TextView = findViewById(R.id.terms_link)
        val insuranceLink: TextView = findViewById(R.id.insurance_link)

        // Set OnClickListener for terms and conditions links
        termsText.setOnClickListener {
            // Action when "Saya setuju dengan" text is clicked (if needed)
        }

        termsLink.setOnClickListener {
            // Open Terms and Conditions link
            openWebPage("https://www.example.com/terms")
        }

        insuranceLink.setOnClickListener {
            // Open Insurance Terms and Conditions link
            openWebPage("https://www.example.com/insurance-terms")
        }

        // Set OnClickListener for pay_button
        payButton.setOnClickListener {
            if (termsCheckbox.isChecked) {
                navigateToBookingActivity()
            } else {
                Toast.makeText(this, "Anda harus menyetujui syarat dan ketentuan terlebih dahulu.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Function to open a web page.
     */
    private fun openWebPage(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = android.net.Uri.parse(url)
        startActivity(intent)
    }

    /**
     * Function to navigate to BookingActivity with the necessary data.
     */
    private fun navigateToBookingActivity() {
        try {
            val intent = Intent(this, BookingActivity::class.java).apply {
                putExtra("venue_name", venueName)
                putExtra("venue_price", venuePrice)
                putExtra("venue_location", venueLocation)
                putExtra("venue_category", venueCategory)
                putExtra("venue_capacity", venueCapacity)
                putExtra("venue_status", venueStatus)
                putExtra("venue_imageUrl", venueImageUrl)
                putExtra("venue_availableStartTime", venueAvailableStartTime)
                putExtra("venue_availableEndTime", venueAvailableEndTime)
                putExtra("booking_date", bookingDate)
                putExtra("booking_time", bookingTime)
                putExtra("courtId", courtId)
                putExtra("time", selectedSlot)
                putExtra("bookingId", bookingId)
            }
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                this,
                getString(R.string.error_booking),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /**
     * Handle back button action on the device.
     */
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}