// DaysAdapter.kt
package com.example.sportsbooking

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DaysAdapter(private var days: List<Day>) : RecyclerView.Adapter<DaysAdapter.DayViewHolder>() {

    inner class DayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dayName: TextView = itemView.findViewById(R.id.day_name)
        val dayDate: TextView = itemView.findViewById(R.id.day_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_day, parent, false)
        return DayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val day = days[position]
        holder.dayName.text = day.dayAbbreviation
        holder.dayDate.text = day.date.toString()
    }

    override fun getItemCount(): Int = days.size

    /**
     * Memperbarui data hari dan memberi tahu adapter bahwa data telah berubah.
     */
    fun updateData(newDays: List<Day>) {
        days = newDays
        notifyDataSetChanged()
    }
}
