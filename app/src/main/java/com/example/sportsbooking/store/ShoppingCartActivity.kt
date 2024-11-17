package com.example.sportsbooking.store

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sportsbooking.R

class ShoppingCartActivity : AppCompatActivity() {

    private lateinit var recyclerViewCart: RecyclerView
    private lateinit var cartAdapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping_cart)

        recyclerViewCart = findViewById(R.id.recyclerViewCart)
        recyclerViewCart.layoutManager = LinearLayoutManager(this)
        cartAdapter = CartAdapter(ShoppingCart.getItems())
        recyclerViewCart.adapter = cartAdapter

        val tvTotalItems = findViewById<TextView>(R.id.tvTotalItems)
        tvTotalItems.text = "Total Items: ${ShoppingCart.getItems().size}"
    }
}