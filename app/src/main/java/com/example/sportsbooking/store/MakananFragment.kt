// MakananFragment.kt
package com.example.sportsbooking.store

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sportsbooking.R

class MakananFragment : Fragment() {

    private lateinit var makananAdapter: MakananAdapter
    private val makananList = listOf(
        Makanan(1, "Nasi Goreng", 5.0),
        Makanan(2, "Mie Goreng", 4.0)
        // Add more items as needed
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_makanan, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerViewMakanan)
        recyclerView.layoutManager = LinearLayoutManager(context)

        makananAdapter = MakananAdapter(makananList) { makanan ->
            // Handle add to cart click
            ShoppingCart.addItem(makanan)
        }
        recyclerView.adapter = makananAdapter

        return view
    }
}