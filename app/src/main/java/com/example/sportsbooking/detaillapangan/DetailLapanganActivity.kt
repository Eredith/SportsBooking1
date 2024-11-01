// DetailLapanganActivity.kt
package com.example.sportsbooking.detaillapangan

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sportsbooking.R
import com.example.sportsbooking.databinding.DetailLapanganBinding
import com.example.sportsbooking.days.Day
import com.example.sportsbooking.days.DaysAdapter
import com.example.sportsbooking.detailpembayaran.DetailPembayaranActivity

class DetailLapanganActivity : AppCompatActivity() {

    private lateinit var binding: DetailLapanganBinding
    private lateinit var recyclerDays: RecyclerView
    private lateinit var daysAdapter: DaysAdapter
    private var daysList: List<Day> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DetailLapanganBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mengambil data dari Intent
        val venueName = intent.getStringExtra("venue_name") ?: "N/A"
        val venuePrice = intent.getStringExtra("venue_price") ?: "N/A"
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
        binding.venuePriceDetail.text = "Harga: $venuePrice"

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

        // Listener untuk links (Opsional)
        binding.termsLink.setOnClickListener {
            openWebPage("https://www.example.com/terms")
        }

        binding.insuranceLink.setOnClickListener {
            openWebPage("https://www.example.com/insurance-terms")
        }
    }

    private fun setupDaysRecyclerView() {
        recyclerDays = binding.recyclerDays
        recyclerDays.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // Initialize sample data
        daysList = getDaysData()
        daysAdapter = DaysAdapter(daysList)
        recyclerDays.adapter = daysAdapter
    }

    private fun getDaysData(): List<Day> {
        return listOf(
            Day("Mon", 25, "Januari"),
            Day("Tue", 26, "Januari"),
            Day("Wed", 27, "Januari"),
            Day("Thu", 28, "Februari"),
            Day("Fri", 29, "Februari"),
            Day("Sat", 30, "Maret"),
            Day("Sun", 31, "Maret")
        )
    }

    /**
     * Menangani aksi saat tombol Book Now diklik
     */
    private fun handleBookNow(
        venueName: String,
        venuePrice: String,
        venueLocation: String,
        venueCategory: String,
        venueCapacity: Int,
        venueStatus: String,
        venueImageUrl: String,
        venueStartTime: String,
        venueEndTime: String
    ) {
        if (binding.termsCheckbox.isChecked) {
            try {
                val intent = Intent(this, DetailPembayaranActivity::class.java).apply {
                    putExtra("venue_name", venueName)
                    putExtra("venue_price", venuePrice)
                    putExtra("venue_location", venueLocation)
                    putExtra("venue_category", venueCategory)
                    putExtra("venue_capacity", venueCapacity)
                    putExtra("venue_status", venueStatus)
                    putExtra("venue_imageUrl", venueImageUrl)
                    putExtra("venue_availableStartTime", venueStartTime)
                    putExtra("venue_availableEndTime", venueEndTime)
                }
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, getString(R.string.error_booking), Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Anda harus menyetujui syarat dan ketentuan terlebih dahulu.", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Fungsi untuk membuka halaman web.
     */
    private fun openWebPage(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = android.net.Uri.parse(url)
        startActivity(intent)
    }
}
