package com.example.sportsbooking.detaillapangan

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.util.Calendar
import java.text.SimpleDateFormat
import com.example.sportsbooking.R
import com.example.sportsbooking.databinding.DetailLapanganBinding
import com.example.sportsbooking.days.Day
import com.example.sportsbooking.days.DaysAdapter
import java.util.Locale

class DetailLapanganActivity : AppCompatActivity() {

    private lateinit var binding: DetailLapanganBinding
    private lateinit var recyclerDays: RecyclerView
    private lateinit var daysAdapter: DaysAdapter
    private var daysList: List<Day> = listOf()
    private var selectedDay: Day? = null
    private var selectedMonth: Int = Calendar.JANUARY
    private var selectedYear: Int = Calendar.getInstance().get(Calendar.YEAR)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DetailLapanganBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mengambil data dari Intent
        val venueName = intent.getStringExtra("venue_name") ?: "N/A"
        val venuePrice = intent.getDoubleExtra("venue_price", 0.0)
        val venueLocation = intent.getStringExtra("venue_location") ?: "N/A"
        val venueCategory = intent.getStringExtra("venue_category") ?: "N/A"
        val venueCapacity = intent.getIntExtra("venue_capacity", 0)
        val venueStatus = intent.getStringExtra("venue_status") ?: "N/A"
        val venueImageUrl = intent.getStringExtra("venue_imageUrl") ?: ""
        val venueStartTime = intent.getStringExtra("venue_availableStartTime") ?: "N/A"
        val venueEndTime = intent.getStringExtra("venue_availableEndTime") ?: "N/A"

        // Populate UI dengan data
        binding.venueTitle.text = "$venueName\n$venueCategory"
        binding.venueAlamat.text = "Alamat: $venueLocation"
//    binding.totalPriceValue.text = "Rp${venuePrice}"

        // Load gambar venue menggunakan Glide
        Glide.with(this)
            .load(venueImageUrl)
            .placeholder(R.drawable.venue_image)
            .error(R.drawable.venue_image)
            .centerCrop()
            .into(binding.venueImage)

        // Setup RecyclerView untuk Days
        setupDaysRecyclerView()

        // Listener untuk tombol back pada toolbar
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        // Listener untuk tombol Book Now
        binding.payButton.setOnClickListener {
            handleBookNow(
                venueName,
                venuePrice,
                venueLocation,
                venueCategory,
                venueCapacity,
                venueStatus,
                venueImageUrl,
                venueStartTime,
                venueEndTime
            )
        }

        // Month Spinner
        val monthSpinner: Spinner = findViewById(R.id.monthSpinner)
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
        val months = resources.getStringArray(R.array.months).sliceArray(currentMonth..11)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, months)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        monthSpinner.adapter = adapter

        // Set listener for month selection
        monthSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedMonth = currentMonth + position // Update selectedMonth based on user selection
                setupDaysRecyclerView() // Refresh days based on new month
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }
    }

    private fun setupDaysRecyclerView() {
        recyclerDays = binding.recyclerDays
        recyclerDays.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // Initialize sample data
        daysList = getDaysData(selectedMonth, selectedYear)
        daysAdapter = DaysAdapter(daysList) { day ->
            onDateSelected(day)
        }
        recyclerDays.adapter = daysAdapter
    }

    private fun getDaysData(selectedMonth: Int, selectedYear: Int): List<Day> {
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        val currentYear = calendar.get(Calendar.YEAR)

        calendar.set(Calendar.MONTH, selectedMonth)
        calendar.set(Calendar.YEAR, selectedYear)
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        val daysList = mutableListOf<Day>()
        for (day in 1..daysInMonth) {
            if (selectedYear == currentYear && (selectedMonth < currentMonth || (selectedMonth == currentMonth && day < currentDay))) {
                continue
            }
            calendar.set(Calendar.DAY_OF_MONTH, day)
            val dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())
            val monthName = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
            daysList.add(Day(dayOfWeek, day, monthName))
        }
        return daysList
    }

    // Panggil fungsi ini saat tanggal dipilih
    private fun onDateSelected(day: Day) {
        selectedDay = day
        val formattedDate = formatDate(day.date, selectedMonth, selectedYear)
        Toast.makeText(this, "Selected date: $formattedDate", Toast.LENGTH_SHORT).show()
    }

    // Fungsi untuk memformat tanggal
    private fun formatDate(day: Int, month: Int, year: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    private fun handleBookNow(
        venueName: String,
        venuePrice: Double,
        venueLocation: String,
        venueCategory: String,
        venueCapacity: Int,
        venueStatus: String,
        venueImageUrl: String,
        venueStartTime: String,
        venueEndTime: String
    ) {
            if (selectedDay == null) {
                Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show()
                return
            }

            try {
                val formattedDate = formatDate(selectedDay!!.date, selectedMonth, selectedYear)
                val intent = Intent(this, DetailLapanganJam::class.java).apply {
                    putExtra("venue_name", venueName)
                    putExtra("venue_price", venuePrice)
                    putExtra("venue_location", venueLocation)
                    putExtra("venue_category", venueCategory)
                    putExtra("venue_capacity", venueCapacity)
                    putExtra("venue_status", venueStatus)
                    putExtra("venue_imageUrl", venueImageUrl)
                    putExtra("venue_availableStartTime", venueStartTime)
                    putExtra("venue_availableEndTime", venueEndTime)
                    putExtra("selected_date", formattedDate)
                }
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, getString(R.string.error_booking), Toast.LENGTH_SHORT).show()
            }
    }

    private fun openWebPage(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = android.net.Uri.parse(url)
        startActivity(intent)
    }
}