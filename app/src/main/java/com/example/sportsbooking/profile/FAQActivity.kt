package com.example.sportsbooking.profile

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.sportsbooking.R

class FAQActivity : AppCompatActivity() {

    private lateinit var cancelButton: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faq) // Directly use the layout

        // Initialize views
        cancelButton = findViewById(R.id.cancelButton)

        cancelButton.setOnClickListener {
            finish() // Close the activity
        }
    }
}


