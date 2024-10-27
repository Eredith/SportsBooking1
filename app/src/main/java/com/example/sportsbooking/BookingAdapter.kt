// BookingAdapter.kt
package com.example.sportsbooking

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class Booking(
    val venueImageResId: Int,
    val venueName: String,
    val venueAddress: String,
    val venueSport: String,
    val bookingStatus: String
)

class BookingAdapter(private val bookingList: List<Booking>) : RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    inner class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageVenue: ImageView = itemView.findViewById(R.id.image_venue)
        val venueName: TextView = itemView.findViewById(R.id.venue_name)
        val venueAddress: TextView = itemView.findViewById(R.id.venue_address)
        val venueSport: TextView = itemView.findViewById(R.id.venue_sport)
        val bookingStatus: TextView = itemView.findViewById(R.id.booking_status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_booking, parent, false)
        return BookingViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = bookingList[position]
        holder.imageVenue.setImageResource(booking.venueImageResId)  // Set image resource
        holder.venueName.text = booking.venueName
        holder.venueAddress.text = booking.venueAddress
        holder.venueSport.text = booking.venueSport
        holder.bookingStatus.text = booking.bookingStatus
    }

    override fun getItemCount(): Int {
        return bookingList.size
    }
}
