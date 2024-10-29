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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var venueAdapter: VenueAdapterMain

    override fun onStart() {
        super.onStart()
        // Cek apakah pengguna sudah login atau belum
        val currentUser = auth.currentUser
        if (currentUser == null) {
            // Jika belum login, arahkan ke LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Supaya MainActivity tidak bisa diakses kembali dengan tombol back
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Mendapatkan UID pengguna yang sedang login
        val user = auth.currentUser
        val usernameTextView = findViewById<TextView>(R.id.username)

        // Mengakses database Firebase untuk mengambil username
        if (user != null) {
            firestore.collection("users").document(user.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        usernameTextView.text = "Halo, " + (document.getString("username") ?: "No Name")
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

        // Inisialisasi RecyclerView dan VenueAdapter
        val recyclerView: RecyclerView = findViewById(R.id.recommendation_recycler_view)
        venueAdapter = VenueAdapterMain(listOf())
        recyclerView.layoutManager = GridLayoutManager(this, 2)  // Menampilkan 2 kolom
        recyclerView.adapter = venueAdapter

        // Mengambil data venue dari Firestore
        fetchVenueData()

        // Tombol Logout
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        btnLogout.setOnClickListener {
            auth.signOut()  // Logout pengguna
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()  // Tutup MainActivity setelah logout
        }

        // Bottom Navigation
        val navHome = findViewById<LinearLayout>(R.id.nav_home)
        val navVenue = findViewById<LinearLayout>(R.id.nav_venue)

        // Listener untuk tombol Home
        navHome.setOnClickListener {
            // Sudah berada di MainActivity, tidak perlu melakukan apa-apa
        }

        // Listener untuk tombol Venue
        navVenue.setOnClickListener {
            val intent = Intent(this, VenueListActivity::class.java)
            startActivity(intent)  // Pindah ke halaman VenueListActivity
        }
    }

    private fun fetchVenueData() {
        firestore.collection("venues")
            .get()
            .addOnSuccessListener { documents ->
                val venueList = mutableListOf<VenueMain>()
                for (document in documents) {
                    // Konversi dokumen Firestore ke objek VenueMain
                    val venue = document.toObject(VenueMain::class.java)
                    venueList.add(venue)
                }
                // Perbarui data di adapter
                venueAdapter.updateData(venueList)
            }
            .addOnFailureListener { e ->
                // Tangani kesalahan saat mengambil data dari Firestore
                Log.e("Firestore", "Error fetching venues", e)
                Toast.makeText(this, "Gagal mengambil data venues", Toast.LENGTH_SHORT).show()
            }
    }
}
