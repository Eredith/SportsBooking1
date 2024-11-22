// DaysAdapter.kt
package com.example.sportsbooking.days

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sportsbooking.R

class DaysAdapter(private var daysList: List<Day>, private val onDateClick: (Day) -> Unit) : RecyclerView.Adapter<DaysAdapter.DayViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION

    inner class DayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dayName: TextView = itemView.findViewById(R.id.day_name)
        val dayNumber: TextView = itemView.findViewById(R.id.day_number)
        val monthName: TextView = itemView.findViewById(R.id.month_name)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    notifyItemChanged(selectedPosition)
                    selectedPosition = position
                    notifyItemChanged(selectedPosition)
                    onDateClick(daysList[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_day, parent, false)
        return DayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val day = daysList[position]
        holder.dayName.text = day.name
        holder.dayNumber.text = day.date.toString()
        holder.monthName.text = day.month

        if (selectedPosition == position) {
            holder.dayName.setTextColor(Color.parseColor("#FFA500")) // Orange color
            holder.dayNumber.setTextColor(Color.parseColor("#FFA500")) // Orange color
            holder.monthName.setTextColor(Color.parseColor("#FFA500")) // Orange color
        } else {
            holder.dayName.setTextColor(Color.BLACK)
            holder.dayNumber.setTextColor(Color.BLACK)
            holder.monthName.setTextColor(Color.BLACK)
        }
    }

    override fun getItemCount(): Int {
        return daysList.size
    }

    fun updateData(newDaysList: List<Day>) {
        daysList = newDaysList
        notifyDataSetChanged()
    }
}