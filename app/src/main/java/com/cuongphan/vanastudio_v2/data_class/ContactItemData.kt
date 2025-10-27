package com.cuongphan.vanastudio_v2.data_class

data class ContactItemData(
    val icon: Int,
    val title: String,
    val action: String,
    val type: ActionType
)

enum class ActionType {
    CALL, CHAT
}