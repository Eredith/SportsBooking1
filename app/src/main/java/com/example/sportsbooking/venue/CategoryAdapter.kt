package com.example.sportsbooking.venue

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sportsbooking.R
import com.example.sportsbooking.venue.Category // Ensure this import path is correct

class CategoryAdapter(
    private val categoryList: List<Category>,
    private val onCategorySelected: (Category) -> Unit // Callback for when a category is selected
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categoryList[position]
        holder.bind(category)

        // Set click listener to handle category selection
        holder.itemView.setOnClickListener {
            onCategorySelected(category)
        }
    }

    override fun getItemCount(): Int = categoryList.size

    // ViewHolder class to bind category data to the view
    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val categoryName: TextView = itemView.findViewById(R.id.category_name)
        private val categoryIcon: ImageView = itemView.findViewById(R.id.category_icon)

        // Bind category data to the view elements
        fun bind(category: Category) {
            categoryName.text = category.name
            categoryIcon.setImageResource(category.iconResId) // Ensure this is correct
        }
    }
}
