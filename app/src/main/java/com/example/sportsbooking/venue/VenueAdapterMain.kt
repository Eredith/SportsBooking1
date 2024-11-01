package com.example.sportsbooking.venue

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

class VenueAdapterMain(private var venueMains: List<VenueMain>) : RecyclerView.Adapter<VenueAdapterMain.VenueViewHolder>() {

    inner class VenueViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val venueImage: ImageView = itemView.findViewById(R.id.venue_image)
        val venueName: TextView = itemView.findViewById(R.id.venue_name)
        val venuePrice: TextView = itemView.findViewById(R.id.venue_price)
        val venueLocation: TextView = itemView.findViewById(R.id.venue_location)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VenueViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_venue_main, parent, false)
        return VenueViewHolder(view)
    }

    override fun onBindViewHolder(holder: VenueViewHolder, position: Int) {
        val venue = venueMains[position]
        holder.venueName.text = venue.name
        holder.venuePrice.text = venue.price
        holder.venueLocation.text = venue.location

        // Memuat gambar menggunakan Glide
        Glide.with(holder.itemView.context)
            .load(venue.imageResource)  // imageResource berisi URL gambar
            .placeholder(R.drawable.venue_image)
            .error(R.drawable.venue_image)
            .into(holder.venueImage)

        // Menambahkan Click Listener pada itemView
        holder.itemView.setOnClickListener {
            // Membuat Intent untuk membuka PageLapanganActivity
            val context = holder.itemView.context
            val intent = Intent(context, PageLapanganActivity::class.java).apply {
                putExtra("venue_name", venue.name)
                putExtra("venue_price", venue.price)
                putExtra("venue_location", venue.location)
                putExtra("venue_category", venue.category)
                putExtra("venue_capacity", venue.capacity)
                putExtra("venue_status", venue.status)
                putExtra("venue_imageUrl", venue.imageResource)
                putExtra("venue_availableStartTime", venue.availableStartTime)
                putExtra("venue_availableEndTime", venue.availableEndTime)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = venueMains.size

    fun updateData(newVenueMains: List<VenueMain>) {
        venueMains = newVenueMains
        notifyDataSetChanged()
    }
}
