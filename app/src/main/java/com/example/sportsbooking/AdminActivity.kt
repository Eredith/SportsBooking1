package com.example.sportsbooking

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import androidx.fragment.app.Fragment
import com.example.sportsbooking.pesanan.BuatPesananFragment
import com.example.sportsbooking.pesanan.LihatPesananFragment

class AdminActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_page)

        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)

        // Tampilkan BuatPesananFragment sebagai default saat pertama kali dibuka
        replaceFragment(BuatPesananFragment())

        // Listener untuk TabLayout
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab?.position == 0) {
                    // Tab "Buat Pesanan" dipilih
                    replaceFragment(BuatPesananFragment())
                } else if (tab?.position == 1) {
                    // Tab "Lihat Pesanan" dipilih
                    replaceFragment(LihatPesananFragment())
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
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
