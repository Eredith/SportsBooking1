package com.example.sportsbooking

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sportsbooking.booking.BookingActivity
import com.example.sportsbooking.login.LoginActivity
import com.example.sportsbooking.profile.ProfileActivity
import com.example.sportsbooking.store.MakananActivity
import com.example.sportsbooking.venue.VenueAdapterMain
import com.example.sportsbooking.venue.VenueListActivity
import com.example.sportsbooking.venue.VenueMain
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var venueAdapter: VenueAdapterMain
    private lateinit var profileImageNavbar: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_main)

        // Firebase initialization
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val usernameTextView = findViewById<TextView>(R.id.username)
        profileImageNavbar = findViewById(R.id.profileImageNavbar) // Get the navbar profile image

        // Fetch user data
        fetchUserProfile(usernameTextView)

        // Load user profile image in navbar
        loadUserProfileImage()

        // Initialize RecyclerView and Adapter
        val recyclerView: RecyclerView = findViewById(R.id.recommendation_recycler_view)
        venueAdapter = VenueAdapterMain(listOf())
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = venueAdapter

        // Fetch venue data
        fetchVenueData()

        // Bottom Navigation
        setupBottomNavigation()
    }

    override fun onResume() {
        super.onResume()
        loadUserProfileImage() // Reload the navbar image when activity resumes
    }

    private fun fetchUserProfile(usernameTextView: TextView) {
        val user = auth.currentUser
        if (user != null) {
            firestore.collection("users").document(user.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        usernameTextView.text = "Halo, ${document.getString("username") ?: "No Name"}"
                    } else {
                        Log.e("Firestore", "Document not found for user: ${user.uid}")
                        Toast.makeText(this, "Profile not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Failed to load profile", e)
                    Toast.makeText(this, "Failed to load profile", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun loadUserProfileImage() {
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            firestore.collection("users").document(user.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val profileImageUrl = document.getString("profileImageUrl")
                        if (!profileImageUrl.isNullOrEmpty()) {
                            Glide.with(this)
                                .load(profileImageUrl)
                                .circleCrop()
                                .placeholder(R.drawable.default_profile)
                                .into(profileImageNavbar)
                        } else {
                            profileImageNavbar.setImageResource(R.drawable.default_profile)
                        }
                    }
                }
                .addOnFailureListener {
                    profileImageNavbar.setImageResource(R.drawable.default_profile)
                }
        }
    }

    private fun fetchVenueData() {
        firestore.collection("venues")
            .get()
            .addOnSuccessListener { documents ->
                val venueList = mutableListOf<VenueMain>()
                for (document in documents) {
                    val venue = document.toObject(VenueMain::class.java)
                    venueList.add(venue)
                }
                venueAdapter.updateData(venueList)
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error fetching venues", e)
                Toast.makeText(this, "Gagal mengambil data venues", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupBottomNavigation() {
        findViewById<LinearLayout>(R.id.nav_home).setOnClickListener {
            // Already in MainActivity, no action needed
        }

        findViewById<LinearLayout>(R.id.nav_venue).setOnClickListener {
            val intent = Intent(this, VenueListActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }

        findViewById<LinearLayout>(R.id.nav_makanan).setOnClickListener {
            val intent = Intent(this, MakananActivity::class.java)
            startActivity(intent)
        }

        findViewById<LinearLayout>(R.id.nav_history).setOnClickListener {
            val intent = Intent(this, BookingActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }

        findViewById<LinearLayout>(R.id.nav_profile).setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
