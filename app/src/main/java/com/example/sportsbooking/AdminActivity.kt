package com.example.sportsbooking

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import androidx.fragment.app.Fragment
import com.example.sportsbooking.pesanan.BuatPesananFragment
import com.example.sportsbooking.pesanan.LihatPesananFragment
import com.example.sportsbooking.pesanan.UploadMakananFragment

class AdminActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_page)

        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        val fragmentContainer = R.id.fragmentContainer

        // Set default fragment
        supportFragmentManager.beginTransaction()
            .replace(fragmentContainer, BuatPesananFragment())
            .commit()

        findViewById<Button>(R.id.btnBackToMain).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // Tampilkan BuatPesananFragment sebagai default saat pertama kali dibuka
        replaceFragment(BuatPesananFragment())

        // Listener untuk TabLayout
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val selectedFragment: Fragment = when (tab.position) {
                    0 -> BuatPesananFragment()
                    1 -> LihatPesananFragment()
                    2 -> UploadMakananFragment()
                    else -> BuatPesananFragment()
                }
                supportFragmentManager.beginTransaction()
                    .replace(fragmentContainer, selectedFragment)
                    .commit()
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    // Fungsi untuk mengganti fragment
    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        fragmentTransaction.commit()
    }
}
