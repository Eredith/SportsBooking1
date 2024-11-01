// DaysAdapter.kt
package com.example.sportsbooking.days

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sportsbooking.databinding.ItemDayBinding

class DaysAdapter(private var daysList: List<Day>) : RecyclerView.Adapter<DaysAdapter.DayViewHolder>() {

    inner class DayViewHolder(private val binding: ItemDayBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(day: Day) {
            binding.dayName.text = day.name
            binding.dayDate.text = day.date.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val binding = ItemDayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DayViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        holder.bind(daysList[position])
    }

    override fun getItemCount(): Int = daysList.size

    // Method to update the days list
    fun updateData(newDaysList: List<Day>) {
        daysList = newDaysList
        notifyDataSetChanged()
    }
}
