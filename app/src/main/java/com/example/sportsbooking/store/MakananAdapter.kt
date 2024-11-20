package com.example.sportsbooking.store

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sportsbooking.R

class MakananAdapter(
    private val makananList: List<Makanan>,
    private val onAddToCartClick: (Makanan) -> Unit
) : RecyclerView.Adapter<MakananAdapter.MakananViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MakananViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_makanan, parent, false)
        return MakananViewHolder(view)
    }

    override fun onBindViewHolder(holder: MakananViewHolder, position: Int) {
        val makanan = makananList[position]
        holder.bind(makanan, onAddToCartClick)
    }

    override fun getItemCount(): Int = makananList.size

    class MakananViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgMakanan: ImageView = itemView.findViewById(R.id.imgMakanan)
        private val tvName: TextView = itemView.findViewById(R.id.tvName)
        private val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        private val btnAddToCart: Button = itemView.findViewById(R.id.btnAddToCart)

        fun bind(makanan: Makanan, onAddToCartClick: (Makanan) -> Unit) {
            tvName.text = makanan.nama
            tvPrice.text = "Price: \$${makanan.harga}"
            Glide.with(itemView.context)
                .load(makanan.imageUrl)
                .into(imgMakanan)
            btnAddToCart.setOnClickListener {
                onAddToCartClick(makanan)
            }
        }
    }
}