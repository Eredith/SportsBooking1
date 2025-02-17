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
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class VenueAdapter(private var venues: List<Venue> = listOf(), private val context: Context) :
    RecyclerView.Adapter<VenueAdapter.VenueViewHolder>() {

    inner class VenueViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.venue_name)
        val priceTextView: TextView = itemView.findViewById(R.id.venue_price)
        val locationTextView: TextView = itemView.findViewById(R.id.venue_location)
        val categoryTextView: TextView = itemView.findViewById(R.id.venue_category)
        val imageView: ImageView = itemView.findViewById(R.id.venue_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VenueViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_venue, parent, false)
        return VenueViewHolder(view)
    }

    override fun onBindViewHolder(holder: VenueViewHolder, position: Int) {
        val venue = venues[position]

        holder.nameTextView.text = venue.nama ?: "N/A"

        val formattedPrice = venue.pricePerHour?.let {
            val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID")) as DecimalFormat
            formatter.applyPattern("Rp #,###")
            formatter.format(it.toDouble())
        } ?: "N/A"

        holder.priceTextView.text = "$formattedPrice"


        holder.locationTextView.text = "Lokasi: ${venue.alamat ?: "N/A"}"
        holder.categoryTextView.text = venue.category ?: "N/A"

        Glide.with(holder.imageView.context)
            .load(venue.imageUrl ?: "") // Fallback for null imageUrl
            .placeholder(R.drawable.venue_image)
            .error(R.drawable.venue_image)
            .into(holder.imageView)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, PageLapanganActivity::class.java).apply {
                putExtra("venue_name", venue.nama ?: "Unknown")
                putExtra("venue_price", venue.pricePerHour ?: "Unknown")
                putExtra("venue_location", venue.alamat ?: "Unknown")
                putExtra("venue_category", venue.category ?: "Unknown")
                putExtra("venue_imageUrl", venue.imageUrl ?: "")

                // Format times safely
                val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
                putExtra(
                    "venue_availableStartTime",
                    venue.availableStartTime?.let { sdf.format(it) } ?: "N/A"
                )
                putExtra(
                    "venue_availableEndTime",
                    venue.availableEndTime?.let { sdf.format(it) } ?: "N/A"
                )
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

