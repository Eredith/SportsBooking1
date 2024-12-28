package com.example.sportsbooking.pesanan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sportsbooking.R

class PesananAdapter(private val pesananList: List<Pesanan>) : RecyclerView.Adapter<PesananAdapter.PesananViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PesananViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pesanan, parent, false)
        return PesananViewHolder(view)
    }

    override fun onBindViewHolder(holder: PesananViewHolder, position: Int) {
        val pesanan = pesananList[position]
        holder.sportsCenter.text = pesanan.sportsCenter
        holder.court.text = pesanan.court
        holder.bookingDate.text = pesanan.bookingDate
        holder.timeSlot.text = pesanan.timeSlot
        holder.bookedBy.text = pesanan.bookedBy
        holder.status.text = pesanan.status
    }

    override fun getItemCount() = pesananList.size

    class PesananViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sportsCenter: TextView = itemView.findViewById(R.id.sportsCenter)
        val court: TextView = itemView.findViewById(R.id.court)
        val bookingDate: TextView = itemView.findViewById(R.id.bookingDate)
        val timeSlot: TextView = itemView.findViewById(R.id.timeSlot)
        val bookedBy: TextView = itemView.findViewById(R.id.bookedBy)
        val status: TextView = itemView.findViewById(R.id.status)
    }
}