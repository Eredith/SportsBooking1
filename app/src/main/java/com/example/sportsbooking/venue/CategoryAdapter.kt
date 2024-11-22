package com.example.sportsbooking.venue

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sportsbooking.R

class CategoryAdapter(
    private var categories: List<Category>,
    private val onItemClick: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val categoryName: TextView = itemView.findViewById(R.id.category_name)
        val categoryIcon: ImageView = itemView.findViewById(R.id.category_icon)

        init {
            itemView.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = adapterPosition
                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)
                onItemClick(categories[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
//        holder.categoryName.text = category.name
        holder.categoryIcon.setImageResource(category.iconResId)

        val isSelected = selectedPosition == position
        holder.itemView.isSelected = isSelected

        // Update icon and text colors
        if (isSelected) {
            holder.categoryIcon.setColorFilter(holder.itemView.context.getColor(R.color.orange))
//            holder.categoryName.setTextColor(holder.itemView.context.getColor(R.color.orange))
        } else {
            holder.categoryIcon.setColorFilter(holder.itemView.context.getColor(R.color.gray))
//            holder.categoryName.setTextColor(holder.itemView.context.getColor(R.color.gray))
        }
    }



    override fun getItemCount(): Int = categories.size

    fun updateData(newCategories: List<Category>) {
        categories = newCategories
        notifyDataSetChanged()
    }
}