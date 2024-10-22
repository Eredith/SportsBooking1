package com.example.sportsbooking

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

class VenueAdapter(private val venueList: List<Venue>) : RecyclerView.Adapter<VenueAdapter.VenueViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VenueViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_layout, parent, false)
        return VenueViewHolder(view)
    }

    override fun onBindViewHolder(holder: VenueViewHolder, position: Int) {
        val venue = venueList[position]
        holder.venueName.text = venue.name
        holder.venuePrice.text = venue.price
        holder.venueLocation.text = venue.location
        holder.venueSport.text = venue.sport
        holder.totalReviews.text = "(${venue.totalReviews})"
        holder.venueImage.setImageResource(venue.imageResource)
    }

    override fun getItemCount(): Int {
        return venueList.size
    }

    class VenueViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val venueImage: ImageView = itemView.findViewById(R.id.venue_image)
        val venueName: TextView = itemView.findViewById(R.id.venue_name)
        val venuePrice: TextView = itemView.findViewById(R.id.venue_price)
        val venueLocation: TextView = itemView.findViewById(R.id.venue_location)
        val venueSport: TextView = itemView.findViewById(R.id.venue_sport)
        val totalReviews: TextView = itemView.findViewById(R.id.total_reviews)
    }
}
