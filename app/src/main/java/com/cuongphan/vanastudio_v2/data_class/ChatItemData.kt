package com.cuongphan.vanastudio_v2.data_class

sealed class ChatItemData{
    data class Message(
        val text: String,
        val isUser: Boolean,
        val timestamp: Long = System.currentTimeMillis()
    ) : ChatItemData()

    data class DateSeparator(val dateText: String) : ChatItemData()
}