package com.example.sportsbooking

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

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

        // Inisialisasi RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.recommendation_recycler_view)

        // Buat data venue
        val venueList = listOf(
            Venue("ASIOP Stadium", "Rp. 1.200.000", "Jakarta", "Sepak Bola", 120, R.drawable.venue_image),
            Venue("Stadion Gelora", "Rp. 900.000", "Surabaya", "Sepak Bola", 80, R.drawable.venue_image)
        )

        // Hubungkan RecyclerView dengan Adapter
        recyclerView.layoutManager = GridLayoutManager(this, 2)  // Menampilkan 2 kolom
        recyclerView.adapter = VenueAdapter(venueList)

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
}
