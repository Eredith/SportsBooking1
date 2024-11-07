package com.example.sportsbooking

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sportsbooking.booking.BookingActivity
import com.example.sportsbooking.login.LoginActivity
import com.example.sportsbooking.profile.ProfileActivity
import com.example.sportsbooking.profile.ProfileFragment
import com.example.sportsbooking.venue.VenueAdapterMain
import com.example.sportsbooking.venue.VenueListActivity
import com.example.sportsbooking.venue.VenueMain
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var venueAdapter: VenueAdapterMain

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Firebase initialization
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val usernameTextView = findViewById<TextView>(R.id.username)

        // Fetch user data
        fetchUserProfile(usernameTextView)

        // Initialize RecyclerView and Adapter
        val recyclerView: RecyclerView = findViewById(R.id.recommendation_recycler_view)
        venueAdapter = VenueAdapterMain(listOf())
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = venueAdapter

        // Fetch venue data
        fetchVenueData()

        // Logout button
        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            logout()
        }

        // Bottom Navigation
        setupBottomNavigation()
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

    private fun logout() {
        auth.signOut()  // Logout pengguna
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()  // Tutup MainActivity setelah logout
    }

    private fun setupBottomNavigation() {
        findViewById<LinearLayout>(R.id.nav_home).setOnClickListener {
            // Already in MainActivity, no action needed
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
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
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
