// BookingAdapter.kt
package com.example.sportsbooking.booking

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sportsbooking.R

class BookingAdapter(private val bookingList: List<Booking>) :
    RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    // ViewHolder untuk setiap item
    inner class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageViewVenue: ImageView = itemView.findViewById(R.id.imageViewVenue)
        val textViewVenueName: TextView = itemView.findViewById(R.id.textViewVenueName)
        val textViewVenueAddress: TextView = itemView.findViewById(R.id.textViewVenueAddress)
        val textViewVenueSport: TextView = itemView.findViewById(R.id.textViewVenueSport)
        val textViewBookingStatus: TextView = itemView.findViewById(R.id.textViewBookingStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        // Inflate layout untuk item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.booking_item, parent, false)
        return BookingViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        // Mengambil data booking pada posisi tertentu
        val booking = bookingList[position]

        // Mengatur data ke ViewHolder
        holder.imageViewVenue.setImageResource(booking.venueImageResId)
        holder.textViewVenueName.text = booking.venueName
        holder.textViewVenueAddress.text = booking.venueAddress
        holder.textViewVenueSport.text = booking.venueSport
        holder.textViewBookingStatus.text = booking.bookingStatus

        // Mengatur warna status berdasarkan status pemesanan
        when (booking.bookingStatus) {
            "Status Pesanan: Berhasil" -> {
                holder.textViewBookingStatus.setTextColor(holder.itemView.context.getColor(android.R.color.holo_green_dark))
            }
            "Status Pesanan: Pending" -> {
                holder.textViewBookingStatus.setTextColor(holder.itemView.context.getColor(android.R.color.holo_orange_dark))
            }
            "Status Pesanan: Dibatalkan" -> {
                holder.textViewBookingStatus.setTextColor(holder.itemView.context.getColor(android.R.color.holo_red_dark))
            }
            else -> {
                holder.textViewBookingStatus.setTextColor(holder.itemView.context.getColor(android.R.color.black))
            }
        }
    }

    override fun getItemCount(): Int {
        return bookingList.size
    }
}
