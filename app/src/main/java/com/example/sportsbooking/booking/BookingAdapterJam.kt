// BookingAdapterJam.kt
package com.example.sportsbooking.booking

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.sportsbooking.R

class BookingAdapterJam(private val onSlotSelected: (BookingSlot) -> Unit) : RecyclerView.Adapter<BookingAdapterJam.BookingViewHolder>() {

    private var slots: List<BookingSlot> = listOf()
    private var selectedPosition: Int = RecyclerView.NO_POSITION

    fun submitList(newSlots: List<BookingSlot>) {
        slots = newSlots
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_booking_slot, parent, false)
        return BookingViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val slot = slots[position]
        holder.bind(slot, position, selectedPosition, onSlotSelected)
    }

    override fun getItemCount(): Int = slots.size

    inner class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val timeTextView: TextView = itemView.findViewById(R.id.slot_time)
        private val priceTextView: TextView = itemView.findViewById(R.id.slot_price)

        fun bind(slot: BookingSlot, position: Int, selectedPosition: Int, onSlotSelected: (BookingSlot) -> Unit) {
            timeTextView.text = slot.time
            priceTextView.text = "Rp${slot.price}"

            if (slot.isBooked) {
                itemView.isEnabled = false
                itemView.alpha = 0.5f
            } else {
                itemView.isEnabled = true
                itemView.alpha = 1.0f
                itemView.setOnClickListener {
                    val previousPosition = this@BookingAdapterJam.selectedPosition
                    this@BookingAdapterJam.selectedPosition = position
                    notifyItemChanged(previousPosition)
                    notifyItemChanged(position)
                    onSlotSelected(slot)
                }
            }

            if (position == selectedPosition) {
                itemView.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.orange))
                timeTextView.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
                priceTextView.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
            } else {
                itemView.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.white))
                timeTextView.setTextColor(ContextCompat.getColor(itemView.context, R.color.black))
                priceTextView.setTextColor(ContextCompat.getColor(itemView.context, R.color.black))
            }
        }
    }
}