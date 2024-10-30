package com.example.sportsbooking

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.*

class VenueAdapter(private var venues: List<Venue>) : RecyclerView.Adapter<VenueAdapter.VenueViewHolder>() {

    class VenueViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.venue_name)
        val priceTextView: TextView = itemView.findViewById(R.id.venue_price)
        val locationTextView: TextView = itemView.findViewById(R.id.venue_location)
        val categoryTextView: TextView = itemView.findViewById(R.id.venue_category)
        val capacityTextView: TextView = itemView.findViewById(R.id.venue_capacity)
        val timeTextView: TextView = itemView.findViewById(R.id.venue_time)
        val imageView: ImageView = itemView.findViewById(R.id.venue_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VenueViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_venue, parent, false)
        return VenueViewHolder(view)
    }

    override fun onBindViewHolder(holder: VenueViewHolder, position: Int) {
        val venue = venues[position]
        holder.nameTextView.text = venue.name
        holder.priceTextView.text = venue.price
        holder.locationTextView.text = venue.location
        holder.categoryTextView.text = venue.category
        holder.capacityTextView.text = "Capacity: ${venue.capacity}"
        holder.timeTextView.text = "${formatTime(venue.availableStartTime)} - ${formatTime(venue.availableEndTime)}"
        // Gunakan Glide untuk memuat gambar dari URL
        Glide.with(holder.itemView.context)
            .load(venue.imageResource)  // imageResource berisi URL gambar
            .into(holder.imageView)
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
