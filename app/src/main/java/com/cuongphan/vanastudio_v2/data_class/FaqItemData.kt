package com.cuongphan.vanastudio_v2.data_class

data class FaqItemData(
    val question: String,
    val answer: String,
    var isExpanded: Boolean = false
)
