package com.example.sportsbooking

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale

class VenueAdapter(private var venues: List<Venue>, private val context: Context) : RecyclerView.Adapter<VenueAdapter.VenueViewHolder>() {

    inner class VenueViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val venueImage: ImageView = itemView.findViewById(R.id.venue_image)
        val venueName: TextView = itemView.findViewById(R.id.venue_name)
        val venuePrice: TextView = itemView.findViewById(R.id.venue_price)
        val venueLocation: TextView = itemView.findViewById(R.id.venue_location)
        val venueCategory: TextView = itemView.findViewById(R.id.venue_category)
        val venueCapacity: TextView = itemView.findViewById(R.id.venue_capacity)
        val venueTime: TextView = itemView.findViewById(R.id.venue_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VenueViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_venue, parent, false)
        return VenueViewHolder(view)
    }

    override fun onBindViewHolder(holder: VenueViewHolder, position: Int) {
        val venue = venues[position]
        holder.venueImage.setImageResource(venue.imageResId)
        holder.venueName.text = venue.name
        holder.venuePrice.text = venue.price
        holder.venueLocation.text = venue.location
        holder.venueCategory.text = venue.category
        holder.venueCapacity.text = "Capacity: ${venue.capacity}"

        // Format waktu mulai dan selesai
        val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
        val startTimeStr = timeFormatter.format(venue.availableStartTime.time)
        val endTimeStr = timeFormatter.format(venue.availableEndTime.time)
        holder.venueTime.text = "$startTimeStr - $endTimeStr"

        // Set onClickListener pada item view untuk berpindah ke VenueDetailActivity
        holder.itemView.setOnClickListener {
            // Intent untuk berpindah ke VenueDetailActivity
            val intent = Intent(context, PageLapanganActivity::class.java).apply {
                putExtra("venue_name", venue.name)
                putExtra("venue_price", venue.price)
                putExtra("venue_location", venue.location)
                putExtra("venue_capacity", venue.capacity)
                putExtra("venue_category", venue.category)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = venues.size

    fun updateData(newVenues: List<Venue>) {
        venues = newVenues
        notifyDataSetChanged()
    }
}
