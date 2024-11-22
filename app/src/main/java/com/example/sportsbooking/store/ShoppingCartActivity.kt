package com.example.sportsbooking.store

import android.os.Bundle
import android.widget.Toast
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

        loadCartData()
    }

    private fun loadCartData() {
        ShoppingCart.loadCartData(
            onSuccess = {
                cartAdapter.notifyDataSetChanged()
                Toast.makeText(this, "Cart data loaded successfully", Toast.LENGTH_SHORT).show()
            },
            onFailure = { e ->
                Toast.makeText(this, "Failed to load cart data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }
}