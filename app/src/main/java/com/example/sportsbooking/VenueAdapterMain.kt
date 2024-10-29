package com.example.sportsbooking

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

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

        // Gunakan Glide untuk memuat gambar dari URL
        Glide.with(holder.itemView.context)
            .load(venue.imageResource)  // imageResource berisi URL gambar
            .into(holder.venueImage)
    }

    override fun getItemCount(): Int = venueMains.size

    fun updateData(newVenueMains: List<VenueMain>) {
        venueMains = newVenueMains
        notifyDataSetChanged()
    }
}
