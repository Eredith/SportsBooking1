package com.example.sportsbooking.booking

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sportsbooking.R

class BookingAdapter(private val bookings: List<Booking>) :
    RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    inner class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val venueName: TextView = itemView.findViewById(R.id.venue_name)
        private val bookingDate: TextView = itemView.findViewById(R.id.booking_date)
        private val bookingTime: TextView = itemView.findViewById(R.id.booking_time)
        private val bookingStatus: TextView = itemView.findViewById(R.id.booking_status)

        fun bind(booking: Booking) {
            venueName.text = booking.venueName
            bookingDate.text = booking.bookingDate
            bookingTime.text = booking.bookingTime
            bookingStatus.text = booking.bookingStatus
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_booking, parent, false)
        return BookingViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        holder.bind(bookings[position])
    }

    override fun getItemCount(): Int = bookings.size
}