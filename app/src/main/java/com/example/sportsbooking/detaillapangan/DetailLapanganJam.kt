// DetailLapanganJam.kt
package com.example.sportsbooking.detaillapangan

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sportsbooking.R
import com.example.sportsbooking.booking.BookingAdapterJam
import com.example.sportsbooking.booking.BookingSlot
import com.example.sportsbooking.detailpembayaran.DetailPembayaranActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DetailLapanganJam : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: BookingAdapterJam
    private var selectedSlot: String? = null
    private var courtId: String? = null

    private var venueName: String? = null
    private var venuePrice: String? = null
    private var venueLocation: String? = null
    private var venueCategory: String? = null
    private var venueCapacity: String? = null
    private var venueStatus: String? = null
    private var venueImageUrl: String? = null
    private var venueAvailableStartTime: String? = null
    private var venueAvailableEndTime: String? = null
    private var selectedDate: String? = null
    private var selectedYear: Int = 0
    private var selectedMonth: Int = 0
    private var selectedDay: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_lapangan)

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance()

        // Initialize views
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        val venueImage: ImageView = findViewById(R.id.venueImage)
        val venueTitle: TextView = findViewById(R.id.venueTitle)
        val venueAlamat: TextView = findViewById(R.id.venueAlamat)
        val priceTextView: TextView = findViewById(R.id.total_price_value)
        val selectedDateTextView: TextView = findViewById(R.id.selectedDateTextView)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val bookButton: Button = findViewById(R.id.payButton)

        // Get data from Intent
        intent?.let {
            venueName = it.getStringExtra("venue_name")
            venuePrice = it.getDoubleExtra("venue_price", 0.0).toString()
            venueLocation = it.getStringExtra("venue_location")
            venueCategory = it.getStringExtra("venue_category")
            venueCapacity = it.getStringExtra("venue_capacity")
            venueStatus = it.getStringExtra("venue_status")
            venueImageUrl = it.getStringExtra("venue_imageUrl")
            venueAvailableStartTime = it.getStringExtra("venue_availableStartTime")
            venueAvailableEndTime = it.getStringExtra("venue_availableEndTime")
            selectedDate = it.getStringExtra("selected_date")
        }

        // Set data to views
        toolbar.title = "Detail Lapangan"
        venueTitle.text = venueName
        venueAlamat.text = venueLocation
        priceTextView.text = "Rp${venuePrice}"
        selectedDate?.let {
            val inputFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormatter = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))

            val parsedDate = inputFormatter.parse(it)
            parsedDate?.let { date ->
                selectedDateTextView.text = outputFormatter.format(date)
            }
        }
        Glide.with(this).load(venueImageUrl).into(venueImage)

        // Set up RecyclerView with GridLayoutManager
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        adapter = BookingAdapterJam { slot ->
            onSlotSelected(slot)
        }
        recyclerView.adapter = adapter

        // Load predefined booking slots
        val predefinedSlots = loadPredefinedSlots()

        // Fetch booked slots from Firestore and update predefined slots
        fetchBookedSlots(predefinedSlots)

        // Book button click listener
        bookButton.setOnClickListener {
            if (selectedSlot == null) {
                Toast.makeText(this, "Please select a time slot", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, DetailPembayaranActivity::class.java).apply {
                    putExtra("venue_name", venueName)
                    putExtra("venue_price", venuePrice)
                    putExtra("venue_location", venueLocation)
                    putExtra("venue_category", venueCategory)
                    putExtra("venue_capacity", venueCapacity)
                    putExtra("venue_status", venueStatus)
                    putExtra("venue_imageUrl", venueImageUrl)
                    putExtra("venue_availableStartTime", venueAvailableStartTime)
                    putExtra("venue_availableEndTime", venueAvailableEndTime)
                    putExtra("selected_date", selectedDate)
                    putExtra("courtId", courtId)
                    putExtra("time", selectedSlot)
                }
                startActivity(intent)
            }
        }
    }
    private fun loadPredefinedSlots(): List<BookingSlot> {
        val slots = mutableListOf<BookingSlot>()
        val startHour = 8
        val endHour = 20
        val price = "100000"

        for (hour in startHour until endHour) {
            val startTime = String.format("%02d:00", hour)
            val endTime = String.format("%02d:00", hour + 1)
            val timeSlot = "$startTime - $endTime"
            slots.add(BookingSlot(timeSlot, price, false))
        }

        return slots
    }

    private fun fetchBookedSlots(predefinedSlots: List<BookingSlot>) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = selectedDate ?: dateFormat.format(Calendar.getInstance().apply {
            set(selectedYear, selectedMonth, selectedDay)
        }.time)

        db.collection("sports_center")
            .document(venueCategory ?: "unknown_category")
            .collection("courts")
            .document(venueName ?: "default_court_id")
            .collection("bookings")
            .document(formattedDate)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val bookedSlots = document.data ?: emptyMap<String, Any>()
                    val updatedSlots = predefinedSlots.map { slot ->
                        if (bookedSlots.containsKey(slot.time)) {
                            slot.copy(isBooked = true)
                        } else {
                            slot
                        }
                    }
                    adapter.submitList(updatedSlots)
                } else {
                    adapter.submitList(predefinedSlots)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to fetch booked slots: ${e.message}", Toast.LENGTH_SHORT).show()
                adapter.submitList(predefinedSlots)
            }
    }
    private fun onSlotSelected(slot: BookingSlot) {
        if (!slot.isBooked) {
            selectedSlot = slot.time
            // Update UI to indicate selected slot if needed
        } else {
            Toast.makeText(this, "This slot is already booked", Toast.LENGTH_SHORT).show()
        }
    }
}