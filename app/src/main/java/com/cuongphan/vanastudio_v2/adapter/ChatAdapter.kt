package com.cuongphan.vanastudio_v2.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cuongphan.vanastudio_v2.data_class.ChatItemData
import com.cuongphan.vanastudio_v2.databinding.ItemDateSeparatorBinding
import com.cuongphan.vanastudio_v2.databinding.ItemMessageBotBinding
import com.cuongphan.vanastudio_v2.databinding.ItemMessageUserBinding

class ChatAdapter(private val items: MutableList<ChatItemData>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_USER = 1
    private val VIEW_BOT = 2
    private val VIEW_DATE = 3

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is ChatItemData.Message -> {
                if ((items[position] as ChatItemData.Message).isUser) VIEW_USER else VIEW_BOT
            }
            is ChatItemData.DateSeparator -> VIEW_DATE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_USER -> UserViewHolder(ItemMessageUserBinding.inflate(inflater, parent, false))
            VIEW_BOT -> BotViewHolder(ItemMessageBotBinding.inflate(inflater, parent, false))
            else -> DateViewHolder(ItemDateSeparatorBinding.inflate(inflater, parent, false))
        }
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is UserViewHolder -> holder.bind(items[position] as ChatItemData.Message)
            is BotViewHolder -> holder.bind(items[position] as ChatItemData.Message)
            is DateViewHolder -> holder.bind(items[position] as ChatItemData.DateSeparator)
        }
    }

    inner class UserViewHolder(private val binding: ItemMessageUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(msg: ChatItemData.Message) {
            binding.tvMessage.text = msg.text
        }
    }

    inner class BotViewHolder(private val binding: ItemMessageBotBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(msg: ChatItemData.Message) {
            binding.tvMessage.text = msg.text
        }
    }

    inner class DateViewHolder(private val binding: ItemDateSeparatorBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(date: ChatItemData.DateSeparator) {
            binding.tvDate.text = date.dateText
        }
    }
}