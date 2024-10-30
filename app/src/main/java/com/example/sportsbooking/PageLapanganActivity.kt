package com.example.sportsbooking

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PageLapanganActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.page_lapangan)

        // Mendapatkan data dari Intent
        val venueName = intent.getStringExtra("venue_name")
        val venuePrice = intent.getStringExtra("venue_price")
        val venueLocation = intent.getStringExtra("venue_location")

        // Inisialisasi button
        val bookNowButton: Button = findViewById(R.id.lapangan_page_button)

        // Set OnClickListener pada button untuk berpindah ke BookingActivity
        bookNowButton.setOnClickListener {
            // Intent untuk membuka BookingActivity
            val intent = Intent(this, DetailPembayaranActivity::class.java)
            startActivity(intent)
        }


//        // Menampilkan data ke TextView
//        findViewById<TextView>(R.id.venue_name).text = venueName
//        findViewById<TextView>(R.id.venue_price).text = venuePrice
//        findViewById<TextView>(R.id.venue_location).text = venueLocation
    }
}
