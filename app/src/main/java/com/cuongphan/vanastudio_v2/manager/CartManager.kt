package com.cuongphan.vanastudio_v2.manager

import android.content.Context
import com.cuongphan.vanastudio_v2.data_class.CartItemData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object CartManager {
    private const val PREFS_NAME = "cart_prefs"
    private const val KEY_CART = "cart_items"
    private const val KEY_TOTAL = "cart_total"

    val cartItems = mutableListOf<CartItemData>()
    var totalAmount: Double = 0.0
        private set

    fun init(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_CART, null)
        if (!json.isNullOrEmpty()) {
            val type = object : TypeToken<List<CartItemData>>() {}.type
            val savedList: List<CartItemData> = Gson().fromJson(json, type)
            cartItems.clear()
            cartItems.addAll(savedList)
        }
    }

    fun addItem(context: Context, item: CartItemData) {
        val existing = cartItems.find {
            it.title == item.title && it.color == item.color && it.size == item.size
        }
        if (existing != null) {
            existing.quantity += item.quantity
        } else {
            cartItems.add(item)
        }
        save(context)
    }

    fun removeItem(context: Context, item: CartItemData) {
        cartItems.remove(item)
        save(context)
    }

    fun clear(context: Context) {
        cartItems.clear()
        save(context)
    }

    fun save(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val json = Gson().toJson(cartItems)
        editor.putString(KEY_CART, json)
        editor.apply()
    }

    fun updateTotal(context: Context, amount: Double) {
        totalAmount = amount
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putFloat(KEY_TOTAL, amount.toFloat()).apply()
    }
}