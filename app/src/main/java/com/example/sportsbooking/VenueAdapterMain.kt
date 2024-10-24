// VenueAdapter.kt
package com.example.sportsbooking

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class VenueAdapterMain(private var venueMains: List<VenueMain>) : RecyclerView.Adapter<VenueAdapterMain.VenueViewHolder>() {

    inner class VenueViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val venueName: TextView = itemView.findViewById(R.id.venue_name)
        // Tambahkan view lainnya sesuai kebutuhan
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VenueViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_venue_main, parent, false)
        return VenueViewHolder(view)
    }

    override fun onBindViewHolder(holder: VenueViewHolder, position: Int) {
        val venue = venueMains[position]
        holder.venueName.text = venue.name
        // Bind data lainnya sesuai kebutuhan
    }

    override fun getItemCount(): Int = venueMains.size

    fun updateData(newVenueMains: List<VenueMain>) {
        venueMains = newVenueMains
        notifyDataSetChanged()
    }
}
