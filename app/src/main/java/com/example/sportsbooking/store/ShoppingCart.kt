package com.example.sportsbooking.store

data class CartItem(val makanan: Makanan, var quantity: Int)

object ShoppingCart {
    private val cartItems = mutableListOf<CartItem>()

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
}