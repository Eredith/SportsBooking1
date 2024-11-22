package com.example.sportsbooking.store

import com.google.firebase.firestore.FirebaseFirestore

data class CartItem(val makanan: Makanan, var quantity: Int)

object ShoppingCart {
    private val cartItems = mutableListOf<CartItem>()
    private val firestore = FirebaseFirestore.getInstance()

    fun addItem(makanan: Makanan) {
        val existingItem = cartItems.find { it.makanan.id == makanan.id }
        if (existingItem != null) {
            existingItem.quantity++
        } else {
            cartItems.add(CartItem(makanan, 1))
        }
    }

    fun getItems(): List<CartItem> = cartItems

    fun clearCart() {
        cartItems.clear()
    }

    fun loadCartData(onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        firestore.collection("cart").get()
            .addOnSuccessListener { result ->
                cartItems.clear()
                for (document in result) {
                    val makanan = document.toObject(Makanan::class.java)
                    val quantity = document.getLong("quantity")?.toInt() ?: 1
                    cartItems.add(CartItem(makanan, quantity))
                }
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }
}