package com.example.sportsbooking.profile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.sportsbooking.R

class FAQActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.faq)

        // Cek apakah fragment sudah ada sebelumnya untuk menghindari replace berulang
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, FAQFragment())
                .commit()
        }
    }
}
