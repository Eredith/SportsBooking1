package com.example.sportsbooking.store

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sportsbooking.MainActivity
import com.example.sportsbooking.R
import com.example.sportsbooking.booking.BookingActivity
import com.example.sportsbooking.profile.ProfileActivity
import com.example.sportsbooking.venue.VenueListActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MakananActivity : AppCompatActivity() {

    private lateinit var recyclerViewMakanan: RecyclerView
    private lateinit var makananAdapter: MakananAdapter
    private val makananList = mutableListOf<Makanan>()
    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var profileImageNavbar: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_makanan)

        recyclerViewMakanan = findViewById(R.id.recyclerViewMakanan)
        recyclerViewMakanan.layoutManager = LinearLayoutManager(this)
        makananAdapter = MakananAdapter(makananList) { makanan ->
            ShoppingCart.addItem(makanan)
            Toast.makeText(this, "${makanan.nama} added to cart", Toast.LENGTH_SHORT).show()
        }
        recyclerViewMakanan.adapter = makananAdapter

        // Inisialisasi ImageView untuk foto profil
        profileImageNavbar = findViewById(R.id.profileImageNavbar)

        // Load foto profil pengguna
        loadUserProfileImage()

        loadMakananData()

        setupBottomNavigation()

    }

    private fun loadMakananData() {
        firestore.collection("makanan").get()
            .addOnSuccessListener { result ->
                makananList.clear()
                for (document in result) {
                    val makanan = document.toObject(Makanan::class.java)
                    makananList.add(makanan)
                }
                makananAdapter.notifyDataSetChanged()
                Log.d("MakananActivity", "Data loaded successfully: ${makananList.size} items")
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to fetch food data: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("MakananActivity", "Error fetching data", e)
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
                                .placeholder(R.drawable.default_profile) // Gambar default jika gagal load
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


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Handle the back button press
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
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
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

        }
        findViewById<LinearLayout>(R.id.nav_venue).setOnClickListener {
            startActivity(Intent(this, VenueListActivity::class.java))
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

        }
        findViewById<LinearLayout>(R.id.nav_makanan).setOnClickListener {

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
}