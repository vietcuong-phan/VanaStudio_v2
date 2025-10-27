package com.cuongphan.vanastudio_v2.data_class

import java.io.Serializable

data class CartItemData(
    val picUrl: String,
    val title: String,
    val color: String?,
    val size: String?,
    var quantity: Int,
    val unitPrice: Double
) : Serializable {
    fun totalPrice(): Double = quantity * unitPrice
}