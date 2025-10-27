package com.cuongphan.vanastudio_v2.data_class

data class Address(
    val title: String = "",
    val details: String = ""
)

data class Profile(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val address: Map<String, Address> = emptyMap(),
    val avatar: String = "",
    val birthday: String = "",
    val gender: String = "",
    val country: String = ""
)