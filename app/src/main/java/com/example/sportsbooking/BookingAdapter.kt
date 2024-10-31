// BookingAdapter.kt
package com.example.sportsbooking

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BookingAdapter(private val onSlotClick: (BookingSlot) -> Unit) : RecyclerView.Adapter<BookingAdapter.BookingSlotViewHolder>() {

    private val bookingSlotList = mutableListOf<BookingSlot>()

    inner class BookingSlotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val slotTime: TextView = itemView.findViewById(R.id.slot_time)
        val slotPrice: TextView = itemView.findViewById(R.id.slot_price)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
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
