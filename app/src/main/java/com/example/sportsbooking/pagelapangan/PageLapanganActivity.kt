// PageLapanganActivity.kt
package com.example.sportsbooking.pagelapangan

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.sportsbooking.detaillapangan.DetailLapanganActivity
import com.example.sportsbooking.R
import com.example.sportsbooking.databinding.PageLapanganBinding

class PageLapanganActivity : AppCompatActivity() {

    private lateinit var binding: PageLapanganBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PageLapanganBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve data from Intent
        val venueName = intent.getStringExtra("venue_name") ?: "N/A"
        val venuePrice = intent.getStringExtra("venue_price") ?: "N/A"
        val venueLocation = intent.getStringExtra("venue_location") ?: "N/A"
        val venueCategory = intent.getStringExtra("venue_category") ?: "N/A"
        val venueCapacity = intent.getIntExtra("venue_capacity", 0)
        val venueStatus = intent.getStringExtra("venue_status") ?: "N/A"
        val venueImageUrl = intent.getStringExtra("venue_imageUrl") ?: ""
        val venueStartTime = intent.getStringExtra("venue_availableStartTime") ?: "N/A"
        val venueEndTime = intent.getStringExtra("venue_availableEndTime") ?: "N/A"

        // Populate UI with data
        binding.venueTitle.text = "$venueName\n$venueCategory"
        binding.venueCategory.text = venueCategory
        binding.venueAlamat.text = "Alamat: $venueLocation"
        binding.venuePrice.text = "Harga: $venuePrice"
        binding.openingHoursTitle.text = getString(R.string.jam_buka)
        binding.openingHoursText.text = "$venueStartTime - $venueEndTime"
        binding.venueLocationText.text = venueLocation
        binding.venueDescriptionDetail.text = "Deskripsi dari alamat $venueName Badminton. Deskripsi dari alamat $venueName Badminton."

        // Load venue image using Glide
        Glide.with(this)
            .load(venueImageUrl)
            .placeholder(R.drawable.venue_image)
            .error(R.drawable.venue_image)
            .centerCrop()
            .into(binding.venueImage)

        // Back button listener
        binding.backbuttonPageLapangan.setOnClickListener {
            finish()
        }

        // Book Now button listener
        binding.lapanganPageButton.setOnClickListener {
            try {
                // Membuat Intent untuk memulai LapanganActivity
                val intent = Intent(this, DetailLapanganActivity::class.java).apply {
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
        }
    }
}
