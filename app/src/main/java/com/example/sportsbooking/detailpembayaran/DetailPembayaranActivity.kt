package com.example.sportsbooking.detailpembayaran

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.example.sportsbooking.booking.BookingActivity
import com.example.sportsbooking.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

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
    private lateinit var db: FirebaseFirestore

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
        db = FirebaseFirestore.getInstance()

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
        payButton.setOnClickListener {
            if (termsCheckbox.isChecked) {
                uploadBookingToFirestore()
            } else {
                Toast.makeText(this, "Anda harus menyetujui syarat dan ketentuan terlebih dahulu.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadBookingToFirestore() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val bookingData = mapOf(
            "booked_by" to userId,
            "status" to "booked"
        )

        val bookingRef = db.collection("sports_center")
            .document(venueCategory ?: "unknown_category")
            .collection("courts")
            .document(courtId ?: "default_court_id")
            .collection("bookings")
            .document(bookingDate ?: "default_date")

        // First, check if the booking document for the selected date exists
        bookingRef.get().addOnSuccessListener { document ->
            if (!document.exists()) {
                // Document for the date does not exist, create it with the selected slot as booked
                val initialSlotData = mapOf((selectedSlot ?: "default_time_slot") to bookingData)
                bookingRef.set(initialSlotData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Booking berhasil!", Toast.LENGTH_SHORT).show()
                        navigateToBookingActivity()
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(this, "Gagal melakukan booking: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                // Document exists, proceed with the transaction to ensure the slot is available
                db.runTransaction { transaction ->
                    val snapshot = transaction.get(bookingRef)
                    val slotData = snapshot.get(selectedSlot ?: "default_time_slot") as? Map<*, *>

                    if (slotData == null || (slotData["available"] == true)) {
                        // If the slot is available or hasn't been booked, proceed with booking
                        transaction.update(bookingRef, selectedSlot ?: "default_time_slot", bookingData)
                    } else {
                        throw Exception("Time slot is already booked.")
                    }
                }.addOnSuccessListener {
                    Toast.makeText(this, "Booking berhasil!", Toast.LENGTH_SHORT).show()
                    navigateToBookingActivity()
                }.addOnFailureListener { exception ->
                    Toast.makeText(this, "Gagal melakukan booking: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Gagal memeriksa ketersediaan: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }


    private fun navigateToBookingActivity() {
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
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
