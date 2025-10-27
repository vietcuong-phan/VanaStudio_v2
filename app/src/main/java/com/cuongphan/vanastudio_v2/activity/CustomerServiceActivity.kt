package com.cuongphan.vanastudio_v2.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.cuongphan.vanastudio_v2.adapter.ChatAdapter
import com.cuongphan.vanastudio_v2.data_class.ChatItemData
import com.cuongphan.vanastudio_v2.databinding.ActivityCustomerServiceBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CustomerServiceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCustomerServiceBinding
    private lateinit var adapter: ChatAdapter
    private val chatItems = mutableListOf<ChatItemData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerServiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ChatAdapter(chatItems)
        binding.chatRcv.layoutManager = LinearLayoutManager(this)
        binding.chatRcv.adapter = adapter

        binding.sendBtn.setOnClickListener {
            val text = binding.messageEdt.text.toString()
            if (text.isNotEmpty()) {
                addMessage(ChatItemData.Message(text, true))
                binding.messageEdt.text.clear()
                autoReply(text)
            }
        }

        binding.goBack.setOnClickListener {
            finish()
        }

        binding.callUs.setOnClickListener {
            val phoneNumber = "0398508235"
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
            startActivity(intent)
        }
    }

    private fun addMessage(message: ChatItemData.Message) {
        if (chatItems.isEmpty() || needNewDateSeparator(message.timestamp)) {
            chatItems.add(ChatItemData.DateSeparator(formatDate(message.timestamp)))
        }
        chatItems.add(message)
        adapter.notifyItemRangeInserted(chatItems.size - 1, 1)
        binding.chatRcv.scrollToPosition(chatItems.size - 1)
    }

    private fun autoReply(userText: String) {
        val reply = when {
            userText.contains("hello", true) -> "Hello 👋, how can I help you?"
            userText.contains("hi", true) -> "Hello 👋, how can I help you?"
            userText.contains("order", true) -> "Do you want to check your order? 📦"
            userText.contains("payment", true) -> "Are you having issues with payment? 💳"
            userText.contains("thanks", true) -> "You're welcome 😃"
            else -> "Sorry, I don't understand 🤔. Can you try again?"
        }
        lifecycleScope.launch {
            delay(3000)
            addMessage(ChatItemData.Message(reply, false))
        }
    }

    private fun needNewDateSeparator(timestamp: Long): Boolean {
        val lastDateSeparator = chatItems.lastOrNull { it is ChatItemData.DateSeparator } as? ChatItemData.DateSeparator
        return lastDateSeparator?.dateText != formatDate(timestamp)
    }

    private fun formatDate(time: Long): String {
        val now = Calendar.getInstance()
        val msgTime = Calendar.getInstance().apply { timeInMillis = time }

        return when {
            isSameDay(now, msgTime) -> "Today"
            isYesterday(msgTime) -> "Yesterday"
            else -> SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(time))
        }
    }

    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    private fun isYesterday(cal: Calendar): Boolean {
        val yesterday = Calendar.getInstance()
        yesterday.add(Calendar.DAY_OF_YEAR, -1)
        return isSameDay(yesterday, cal)
    }
}