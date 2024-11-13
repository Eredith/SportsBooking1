package com.example.sportsbooking.store

// MakananAdapter.kt
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sportsbooking.R

class MakananAdapter(private val makananList: List<Makanan>) :
    RecyclerView.Adapter<MakananAdapter.MakananViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MakananViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_makanan, parent, false)
        return MakananViewHolder(view)
    }

    override fun onBindViewHolder(holder: MakananViewHolder, position: Int) {
        val makanan = makananList[position]
        holder.bind(makanan)
    }

    override fun getItemCount(): Int = makananList.size

    inner class MakananViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val namaMakanan: TextView = itemView.findViewById(R.id.namaMakanan)
        private val hargaMakanan: TextView = itemView.findViewById(R.id.hargaMakanan)
        private val imageMakanan: ImageView = itemView.findViewById(R.id.imageMakanan)

        fun bind(makanan: Makanan) {
            namaMakanan.text = makanan.nama
            hargaMakanan.text = "Rp ${makanan.harga}"
            Glide.with(itemView.context)
                .load(makanan.imageUrl)
                .into(imageMakanan)
        }
    }
}
