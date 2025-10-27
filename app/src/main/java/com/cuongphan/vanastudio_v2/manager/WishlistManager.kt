package com.cuongphan.vanastudio_v2.manager

import com.cuongphan.vanastudio_v2.data_class.ProductData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

object WishlistManager {
    private val wishlist = mutableListOf<ProductData>()
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().getReference("Customers")

    fun addToWishlist(product: ProductData) {
        if (wishlist.none { it.id == product.id }) {
            wishlist.add(product)
        }
        val uid = auth.currentUser?.uid ?: return
        val wishlistRef = database.child(uid).child("wishlist").child(product.id.toString())
        wishlistRef.setValue(product)
    }

    fun removeFromWishlist(product: ProductData) {
        wishlist.removeAll { it.id == product.id }
        val uid = auth.currentUser?.uid ?: return
        val wishlistRef = database.child(uid).child("wishlist").child(product.id.toString())
        wishlistRef.removeValue()
    }

    fun getWishlist(): List<ProductData> = wishlist

    fun isInWishlist(product: ProductData): Boolean {
        return wishlist.any { it.id == product.id }
    }
}