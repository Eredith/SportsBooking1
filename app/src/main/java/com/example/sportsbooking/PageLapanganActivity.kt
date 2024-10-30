package com.example.sportsbooking

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class PageLapanganActivity : AppCompatActivity() {

    // Deklarasi variabel UI dengan lateinit
    private lateinit var tvVenueTitle: TextView
    private lateinit var tvVenueAlamat: TextView
    private lateinit var tvVenueCategory: TextView
    private lateinit var tvVenuePrice: TextView
    private lateinit var tvOpeningHoursTitle: TextView
    private lateinit var tvOpeningHours: TextView
    private lateinit var tvDetailAlamatTitle: TextView
    private lateinit var imgVenueLocation: ImageView
    private lateinit var tvVenueLocation: TextView
    private lateinit var tvVenueDescriptionDetail: TextView
    private lateinit var imgVenue: ImageView
    private lateinit var backButton: ImageButton
    private lateinit var bookNowButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.page_lapangan) // Pastikan nama layout sesuai

        // Inisialisasi variabel UI dengan findViewById
        backButton = findViewById(R.id.backbutton_page_lapangan)
        imgVenue = findViewById(R.id.venue_image)
        tvVenueTitle = findViewById(R.id.venue_title)
        tvVenueAlamat = findViewById(R.id.venue_alamat)
        tvVenueCategory = findViewById(R.id.venue_category)
        tvVenuePrice = findViewById(R.id.venue_price) // Inisialisasi tvVenuePrice
        tvOpeningHoursTitle = findViewById(R.id.opening_hours_title)
        tvOpeningHours = findViewById(R.id.opening_hours_text)
        tvDetailAlamatTitle = findViewById(R.id.detail_alamat_title)
        imgVenueLocation = findViewById(R.id.venue_location_image)
        tvVenueLocation = findViewById(R.id.venue_location_text)
        tvVenueDescriptionDetail = findViewById(R.id.venue_description_detail)
        bookNowButton = findViewById(R.id.lapangan_page_button)

        // Mendapatkan data dari Intent
        val venueName = intent.getStringExtra("venue_name") ?: "N/A"
        val venuePrice = intent.getStringExtra("venue_price") ?: "N/A"
        val venueLocation = intent.getStringExtra("venue_location") ?: "N/A"
        val venueCategory = intent.getStringExtra("venue_category") ?: "N/A"
        val venueCapacity = intent.getIntExtra("venue_capacity", 0)
        val venueStatus = intent.getStringExtra("venue_status") ?: "N/A"
        val venueImageUrl = intent.getStringExtra("venue_imageUrl") ?: ""
        val venueStartTime = intent.getStringExtra("venue_availableStartTime") ?: "N/A"
        val venueEndTime = intent.getStringExtra("venue_availableEndTime") ?: "N/A"

        // Menampilkan data ke UI
        tvVenueTitle.text = "$venueName\n$venueCategory"
        tvVenueCategory.text = "$venueCategory"
        tvVenueAlamat.text = "Alamat: $venueLocation"
        tvVenuePrice.text = "Harga: $venuePrice"
        tvOpeningHoursTitle.text = "Jam Buka"
        tvOpeningHours.text = "$venueStartTime - $venueEndTime"
        tvVenueLocation.text = venueLocation
        tvVenueDescriptionDetail.text = "Deskripsi dari alamat $venueName Badminton. Deskripsi dari alamat $venueName Badminton."

        // Memuat gambar venue menggunakan Glide
        Glide.with(this)
            .load(venueImageUrl)
            .placeholder(R.drawable.venue_image)
            .error(R.drawable.venue_image)
            .into(imgVenue)

        // Listener untuk tombol kembali
        backButton.setOnClickListener {
            finish()
        }

        // Listener untuk tombol "Book Now"
        bookNowButton.setOnClickListener {
            // Intent untuk membuka DetailPembayaranActivity dengan data venue
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
        }
    }
}
