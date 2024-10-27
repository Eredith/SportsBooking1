// BookingActivity.kt
package com.example.sportsbooking

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class DetailPembayaranActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_pembayaran)

        // Setup any necessary logic for booking here
        // Inisialisasi button
        val payNowButton: Button = findViewById(R.id.pay_button)

        // Set OnClickListener pada button untuk berpindah ke BookingActivity
        payNowButton.setOnClickListener {
            // Intent untuk membuka BookingActivity
            val intent = Intent(this, BookingActivity::class.java)
            startActivity(intent)
        }
    }
}
