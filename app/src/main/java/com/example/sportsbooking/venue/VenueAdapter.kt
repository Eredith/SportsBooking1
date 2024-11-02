// VenueAdapter.kt
package com.example.sportsbooking.venue

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sportsbooking.pagelapangan.PageLapanganActivity
import com.example.sportsbooking.R
import java.text.SimpleDateFormat
import java.util.*

class VenueAdapter(private var venues: List<Venue>, private val context: Context) : RecyclerView.Adapter<VenueAdapter.VenueViewHolder>() {

    inner class VenueViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.venue_name)
        val priceTextView: TextView = itemView.findViewById(R.id.venue_price)
        val locationTextView: TextView = itemView.findViewById(R.id.venue_location)
        val categoryTextView: TextView = itemView.findViewById(R.id.venue_category)
        val capacityTextView: TextView = itemView.findViewById(R.id.venue_capacity)
        val statusTextView: TextView = itemView.findViewById(R.id.venue_status)
        val timeTextView: TextView = itemView.findViewById(R.id.venue_time)
        val imageView: ImageView = itemView.findViewById(R.id.venue_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VenueViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_venue, parent, false)
        return VenueViewHolder(view)
    }

    override fun onBindViewHolder(holder: VenueViewHolder, position: Int) {
        val venue = venues[position]
        holder.nameTextView.text = venue.nama // Pastikan menggunakan nama yang benar
        holder.priceTextView.text = "Harga: ${venue.pricePerHour}" // Pastikan menggunakan pricePerHour
        holder.locationTextView.text = "Lokasi: ${venue.alamat}" // Pastikan menggunakan alamat
        holder.categoryTextView.text = "Kategori: ${venue.category}"
        holder.capacityTextView.text = "Kapasitas: ${venue.capacity}" // Pastikan ada atribut kapasitas
        holder.statusTextView.text = "Status: ${venue.status}"
        holder.timeTextView.text = "Waktu: ${formatTime(venue.availableStartTime)} - ${formatTime(venue.availableEndTime)}"

        // Menggunakan Glide untuk memuat gambar dari URL
        Glide.with(holder.imageView.context)
            .load(venue.imageUrl) // Menggunakan imageUrl dari model Venue
            .placeholder(R.drawable.venue_image)
            .error(R.drawable.venue_image)
            .into(holder.imageView)

        // Menambahkan Listener untuk Klik pada Item
        holder.itemView.setOnClickListener {
            val intent = Intent(context, PageLapanganActivity::class.java).apply {
                putExtra("venue_name", venue.nama) // Menggunakan nama yang benar
                putExtra("venue_price", venue.pricePerHour) // Menggunakan pricePerHour
                putExtra("venue_location", venue.alamat) // Menggunakan alamat
                putExtra("venue_category", venue.category)
                putExtra("venue_capacity", venue.capacity) // Pastikan ada atribut kapasitas
                putExtra("venue_status", venue.status)
                putExtra("venue_imageUrl", venue.imageUrl) // Menggunakan imageUrl dari model Venue

                // Format waktu menjadi string jika diperlukan
                val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
                val startTime = venue.availableStartTime?.let { sdf.format(it) } ?: "N/A"
                val endTime = venue.availableEndTime?.let { sdf.format(it) } ?: "N/A"
                putExtra("venue_availableStartTime", startTime)
                putExtra("venue_availableEndTime", endTime)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = venues.size

    private fun formatTime(date: Date?): String {
        if (date == null) return "N/A"
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(date)
    }

    // Fungsi untuk memperbarui data dan memberitahu adapter
    fun updateData(newVenues: List<Venue>) {
        venues = newVenues
        notifyDataSetChanged()
    }
}
