package com.cuongphan.vanastudio_v2.data_class

import java.io.Serializable

data class OrderData(
    val orderId: String? = null,
    val productName: String? = null,
    val price: Double? = null,
    val quantity: Int? = null,
    val size: String? = null,
    val color: String? = null,
    val paymentMethod: String? = null,
    val orderDate: String? = null,
    val productImage: String? = null
) : Serializable