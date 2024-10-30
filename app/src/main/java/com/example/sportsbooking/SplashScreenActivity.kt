package com.example.sportsbooking

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inisialisasi FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Cek apakah user sudah login
        val currentUser = auth.currentUser
        if (currentUser == null) {
            // Jika user belum login, arahkan ke LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Agar SplashScreenActivity tidak dapat diakses kembali
        } else {
            // Jika user sudah login, arahkan ke MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Agar SplashScreenActivity tidak dapat diakses kembali
        }
    }
}
