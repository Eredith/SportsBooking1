package com.example.sportsbooking.booking

import Booking
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sportsbooking.R

class BookingAdapter(private val bookings: List<Booking>) : RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sportsCenter: TextView = itemView.findViewById(R.id.sportsCenter)
        val court: TextView = itemView.findViewById(R.id.court)
        val tvBookingTime: TextView = itemView.findViewById(R.id.tv_booking_time)
        val tvBookingDate: TextView = itemView.findViewById(R.id.tv_booking_date)
        val bookedBy: TextView = itemView.findViewById(R.id.bookedBy)
        val status: TextView = itemView.findViewById(R.id.status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_booking, parent, false)
        return BookingViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = bookings[position]
        holder.sportsCenter.text = booking.sportsCenter
        holder.court.text = booking.court
        holder.tvBookingTime.text = booking.timeSlot
        holder.tvBookingDate.text = booking.bookingDate
        holder.bookedBy.text = booking.bookedBy
        holder.status.text = booking.status
    }

    override fun getItemCount() = bookings.size
}