package com.example.sportsbooking.store

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sportsbooking.R
import com.google.firebase.firestore.FirebaseFirestore

class MakananActivity : AppCompatActivity() {

    private lateinit var recyclerViewMakanan: RecyclerView
    private lateinit var makananAdapter: MakananAdapter
    private val makananList = mutableListOf<Makanan>()
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_makanan)

        // Set up the back button functionality
        val ivBack = findViewById<ImageView>(R.id.ivBack)
        ivBack.setOnClickListener {
            onBackPressed()
        }

        recyclerViewMakanan = findViewById(R.id.recyclerViewMakanan)
        recyclerViewMakanan.layoutManager = LinearLayoutManager(this)
        makananAdapter = MakananAdapter(makananList) { makanan ->
            ShoppingCart.addItem(makanan)
            Toast.makeText(this, "${makanan.nama} added to cart", Toast.LENGTH_SHORT).show()
        }
        recyclerViewMakanan.adapter = makananAdapter

        loadMakananData()

        val btnViewCart = findViewById<Button>(R.id.btnViewCart)
        btnViewCart.setOnClickListener {
            startActivity(Intent(this, ShoppingCartActivity::class.java))
        }

        // Set up the shopping cart button functionality
        val btnShoppingCart = findViewById<Button>(R.id.btnShoppingCart)
        btnShoppingCart.setOnClickListener {
            startActivity(Intent(this, ShoppingCartActivity::class.java))
        }
    }

    private fun loadMakananData() {
        firestore.collection("makanan").get()
            .addOnSuccessListener { result ->
                makananList.clear()
                for (document in result) {
                    val makanan = document.toObject(Makanan::class.java)
                    makananList.add(makanan)
                }
                makananAdapter.notifyDataSetChanged()
                Log.d("MakananActivity", "Data loaded successfully: ${makananList.size} items")
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to fetch food data: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("MakananActivity", "Error fetching data", e)
            }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Handle the back button press
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}