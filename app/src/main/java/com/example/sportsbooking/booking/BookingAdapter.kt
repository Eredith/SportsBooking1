// BookingAdapter.kt
package com.example.sportsbooking.booking

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sportsbooking.R

class BookingAdapter(private val onSlotClick: (BookingSlot) -> Unit) : RecyclerView.Adapter<BookingAdapter.BookingSlotViewHolder>() {

    private val bookingSlotList = mutableListOf<BookingSlot>()
    private var selectedPosition = RecyclerView.NO_POSITION

    inner class BookingSlotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val slotTime: TextView = itemView.findViewById(R.id.slot_time)
        val slotPrice: TextView = itemView.findViewById(R.id.slot_price)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    notifyItemChanged(selectedPosition)
                    selectedPosition = position
                    notifyItemChanged(selectedPosition)
                    val bookingSlot = bookingSlotList[position]
                    onSlotClick(bookingSlot)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingSlotViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_booking_slot, parent, false)
        return BookingSlotViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookingSlotViewHolder, position: Int) {
        val slot = bookingSlotList[position]
        holder.slotTime.text = slot.time
        holder.slotPrice.text = slot.price

        if (position == selectedPosition) {
            holder.itemView.setBackgroundColor(Color.parseColor("#FFA500")) // Orange background
            holder.slotTime.setTextColor(Color.WHITE) // White text
            holder.slotPrice.setTextColor(Color.WHITE) // White text
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT) // Default background
            holder.slotTime.setTextColor(Color.BLACK) // Default text color
            holder.slotPrice.setTextColor(Color.BLACK) // Default text color
        }
    }

    override fun getItemCount(): Int {
        return bookingSlotList.size
    }

    fun submitList(slots: List<BookingSlot>) {
        bookingSlotList.clear()
        bookingSlotList.addAll(slots)
        notifyDataSetChanged()
    }
}