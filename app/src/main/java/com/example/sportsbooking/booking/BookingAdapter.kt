package com.example.sportsbooking.booking

import Booking // Pastikan import kelas Booking kamu benar
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sportsbooking.R
import com.example.sportsbooking.detailtransaksi.DetailTransaksiActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

        // Logging untuk memastikan onBindViewHolder dipanggil dan data ada
        Log.d("BookingAdapter", "onBindViewHolder called for position: $position")
        Log.d("BookingAdapter", "Booking data: $booking")

        holder.sportsCenter.text = booking.sportsCenter
        holder.court.text = booking.court
        holder.tvBookingTime.text = booking.timeSlot
        holder.tvBookingDate.text = formatBookingDate(booking.bookingDate)
        holder.bookedBy.text = booking.bookedBy
        holder.status.text = booking.status

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            Log.d("BookingAdapter", "Item clicked at position: $position") // Log saat item di-klik

            val intent = Intent(context, DetailTransaksiActivity::class.java).apply {
                putExtra("category", booking.sportsCenter)
                putExtra("court", booking.court)
                putExtra("bookingDate", booking.bookingDate)
                putExtra("timeSlot", booking.timeSlot)
                putExtra("username", booking.bookedBy)
                putExtra("status", booking.status)
                // Log data yang dikirimkan ke DetailTransaksiActivity
                Log.d("BookingAdapter", "Intent putExtra - category: ${booking.sportsCenter}")
                Log.d("BookingAdapter", "Intent putExtra - court: ${booking.court}")
                Log.d("BookingAdapter", "Intent putExtra - bookingDate: ${booking.bookingDate}")
                Log.d("BookingAdapter", "Intent putExtra - timeSlot: ${booking.timeSlot}")
                Log.d("BookingAdapter", "Intent putExtra - username: ${booking.bookedBy}")
                Log.d("BookingAdapter", "Intent putExtra - status: ${booking.status}")
            }
            context.startActivity(intent)
            Log.d("BookingAdapter", "startActivity called") // Log setelah startActivity dipanggil
        }
    }

    override fun getItemCount() = bookings.size

    // Fungsi untuk memformat bookingDate menjadi format yang diinginkan
    private fun formatBookingDate(dateString: String): String {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())  // Format input (yyyy-MM-dd)
            val date = sdf.parse(dateString)  // Parse string ke objek Date
            val outputFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))  // Format yang diinginkan (e.g., "Rabu, 25 September 2025")
            outputFormat.format(date ?: Date())  // Mengembalikan tanggal yang sudah diformat
        } catch (e: Exception) {
            "Tanggal tidak valid"
        }
    }
}