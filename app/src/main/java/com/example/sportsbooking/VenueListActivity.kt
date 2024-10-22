package com.example.sportsbooking

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class VenueListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_venue_list)

        // Buat data venue
        val venueList = listOf(
            Venue("ASIOP Stadium", "Rp. 1.200.000", "Jakarta", "Sepak Bola", 120, R.drawable.venue_image),
            Venue("Stadion Gelora", "Rp. 900.000", "Surabaya", "Sepak Bola", 80, R.drawable.venue_image)
        )

        val recyclerView = findViewById<RecyclerView>(R.id.venueRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = VenueAdapter(venueList)
    }
}
