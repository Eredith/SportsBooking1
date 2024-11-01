package com.example.sportsbooking.pesanan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sportsbooking.R

class PesananAdapter(
    private val pesananList: List<Pesanan>
) : RecyclerView.Adapter<PesananAdapter.PesananViewHolder>() {

    class PesananViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val fieldImage: ImageView = view.findViewById(R.id.fieldImage)
        val fieldName: TextView = view.findViewById(R.id.fieldName)
        val fieldLocation: TextView = view.findViewById(R.id.fieldLocation)
        val fieldType: TextView = view.findViewById(R.id.fieldType)
        val fieldStatus: TextView = view.findViewById(R.id.fieldStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PesananViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pesanan, parent, false)
        return PesananViewHolder(view)
    }

    override fun onBindViewHolder(holder: PesananViewHolder, position: Int) {
        val pesanan = pesananList[position]

        // Mengisi data ke dalam CardView
        holder.fieldName.text = pesanan.nama
        holder.fieldLocation.text = pesanan.alamat
        holder.fieldType.text = pesanan.jenisLapangan
        holder.fieldStatus.text = pesanan.status

        // Mengambil gambar menggunakan Glide
        Glide.with(holder.itemView.context)
            .load(pesanan.imageUrl)
            .into(holder.fieldImage)
    }

    override fun getItemCount(): Int {
        return pesananList.size
    }
}
