package com.example.sportsbooking

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class Category(
    val name: String,
    val icon: Int, // Drawable resource ID
    val month: String
)

class CategoryAdapter(private var categories: List<Category>) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.category_icon)
        val name: TextView = itemView.findViewById(R.id.category_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.icon.setImageResource(category.icon)
        holder.name.text = category.name
    }

    override fun getItemCount(): Int = categories.size

    /**
     * Memperbarui data kategori dan memberi tahu adapter bahwa data telah berubah.
     */
    fun updateData(newCategories: List<Category>) {
        categories = newCategories
        notifyDataSetChanged()
    }
}
