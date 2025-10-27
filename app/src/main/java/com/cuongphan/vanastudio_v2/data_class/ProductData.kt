package com.cuongphan.vanastudio_v2.data_class

import java.io.Serializable

data class ProductData(
    val id: Int? = null,
    val title: String? = null,
    val sold: Int? = null,
    val rating: Double? = null,
    val review: Int? = null,
    val size: ArrayList<String>? = null,
    val color: ArrayList<String>? = null,
    val category_id: Int? = null,
    val picUrl: String? = null,
    val price: Double? = null,
    val description: String? = null,
    val recommend: Boolean? = null
) : Serializable