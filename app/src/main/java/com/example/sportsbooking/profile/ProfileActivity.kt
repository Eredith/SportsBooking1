package com.example.sportsbooking.profile

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.sportsbooking.MainActivity
import com.example.sportsbooking.R
import com.example.sportsbooking.booking.BookingActivity
import com.example.sportsbooking.venue.VenueListActivity
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {

    private lateinit var profileImageView: ImageView
    private lateinit var nameTextView: TextView
    private lateinit var emailTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Initialize the views
        profileImageView = findViewById(R.id.profileImageView)
        nameTextView = findViewById(R.id.nameTextView)
        emailTextView = findViewById(R.id.emailTextView)
        setupBottomNavigation()
        // Load user data from Firebase Authentication
        loadUserData()
    }

    private fun loadUserData() {
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            // Display Name
            val name = user.displayName
            nameTextView.text = name ?: "N/A"

            // Email
            val email = user.email
            emailTextView.text = email ?: "N/A"

            // Profile Picture
            val photoUrl = user.photoUrl
            if (photoUrl != null) {
                // Load profile picture using Glide
                Glide.with(this)
                    .load(photoUrl)
                    .placeholder(R.drawable.default_profile) // Replace with a local default image
                    .into(profileImageView)
            } else {
                // Set default profile image if no URL is available
                profileImageView.setImageResource(R.drawable.default_profile)
            }
        }
    }
    private fun setupBottomNavigation() {
        findViewById<LinearLayout>(R.id.nav_home).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        findViewById<LinearLayout>(R.id.nav_venue).setOnClickListener {
            val intent = Intent(this, VenueListActivity::class.java)
            startActivity(intent)
        }

        findViewById<LinearLayout>(R.id.nav_history).setOnClickListener {
            val intent = Intent(this, BookingActivity::class.java)
            startActivity(intent)
        }

        findViewById<LinearLayout>(R.id.nav_profile).setOnClickListener {
        }
    }
}
