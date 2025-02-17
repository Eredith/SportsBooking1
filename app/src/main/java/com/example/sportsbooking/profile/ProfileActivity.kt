package com.example.sportsbooking.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.sportsbooking.AdminActivity
import com.example.sportsbooking.MainActivity
import com.example.sportsbooking.R
import com.example.sportsbooking.booking.BookingActivity
import com.example.sportsbooking.login.LoginActivity
import com.example.sportsbooking.store.MakananActivity
import com.example.sportsbooking.venue.VenueListActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {

    private lateinit var profileImageView: ImageView
    //*private lateinit var nameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var usernameTextView: TextView
    private lateinit var profileImageNavbar: ImageView
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Initialize views
        profileImageView = findViewById(R.id.profileImageView)
        //*nameTextView = findViewById(R.id.nameTextView)
        emailTextView = findViewById(R.id.emailTextView)
        usernameTextView = findViewById(R.id.usernameTextView)
        profileImageNavbar = findViewById(R.id.profileImageNavbar)


        setupBottomNavigation()
        loadUserData() // Load user data initially

        setupMenuClickListeners()
    }

    override fun onResume() {
        super.onResume()
        // Reload user data every time the activity resumes
        loadUserData()
    }

    private fun loadUserData() {
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            // Display Name
            //* val name = user.displayName
            //* nameTextView.text = name ?: "N/A"

            // Email
            val email = user.email
            emailTextView.text = email ?: "N/A"

            // Retrieve username, role, and profile image URL from Firestore
            db.collection("users").document(user.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        // Username
                        val username = document.getString("username")
                        usernameTextView.text = username ?: "N/A"

                        // Role (Admin or not)
                        val isAdmin = document.getBoolean("isAdmin") ?: false
                        if (isAdmin) {
                            findViewById<Button>(R.id.btnBackToAdmin).visibility = View.VISIBLE
                        } else {
                            findViewById<Button>(R.id.btnBackToAdmin).visibility = View.GONE
                        }

                        // Profile Picture
                        val profileImageUrl = document.getString("profileImageUrl")
                        if (!profileImageUrl.isNullOrEmpty()) {
                            Glide.with(this)
                                .load(profileImageUrl)
                                .circleCrop()
                                .placeholder(R.drawable.default_profile)
                                .into(profileImageView)

                            // Set the same image for the navbar
                            Glide.with(this)
                                .load(profileImageUrl)
                                .circleCrop()
                                .placeholder(R.drawable.default_profile)
                                .into(profileImageNavbar)
                        } else {
                            profileImageView.setImageResource(R.drawable.default_profile)
                            profileImageNavbar.setImageResource(R.drawable.default_profile)
                        }
                    } else {
                        usernameTextView.text = "N/A"
                        profileImageView.setImageResource(R.drawable.default_profile)
                    }
                }
                .addOnFailureListener {
                    usernameTextView.text = "Error loading username"
                    profileImageView.setImageResource(R.drawable.default_profile)
                    profileImageNavbar.setImageResource(R.drawable.default_profile)
                }
        }
    }

    private fun setupMenuClickListeners() {
        findViewById<View>(R.id.settingsMenu).setOnClickListener {
            navigateToActivity(SettingsActivity::class.java)
        }
        findViewById<View>(R.id.faqMenu).setOnClickListener {
            navigateToActivity(FAQActivity::class.java)
        }
    }

    private fun navigateToActivity(targetActivity: Class<*>) {
        startActivity(
            Intent(this, targetActivity).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
        )
    }

    private fun setupBottomNavigation() {
        findViewById<LinearLayout>(R.id.nav_home).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.nav_venue).setOnClickListener {
            startActivity(Intent(this, VenueListActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.nav_makanan).setOnClickListener {
            val intent = Intent(this, MakananActivity::class.java)
            startActivity(intent)
        }
        findViewById<LinearLayout>(R.id.nav_history).setOnClickListener {
            startActivity(Intent(this, BookingActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.nav_profile).setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }
}