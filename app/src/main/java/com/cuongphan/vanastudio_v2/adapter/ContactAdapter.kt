package com.cuongphan.vanastudio_v2.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cuongphan.vanastudio_v2.activity.CustomerServiceActivity
import com.cuongphan.vanastudio_v2.data_class.ActionType
import com.cuongphan.vanastudio_v2.data_class.ContactItemData
import com.cuongphan.vanastudio_v2.databinding.ItemContactBinding

class ContactAdapter(
    private val context: Context,
    private val items: List<ContactItemData>
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    inner class ContactViewHolder(val binding: ItemContactBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ContactItemData) {
            binding.imgIcon.setImageResource(item.icon)
            binding.tvTitle.text = item.title

            binding.root.setOnClickListener {
                when (item.type) {
                    ActionType.CALL -> {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse(item.action))
                        context.startActivity(intent)
                    }
                    ActionType.CHAT -> {
                        val intent = Intent(context, CustomerServiceActivity::class.java)
                        context.startActivity(intent)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding = ItemContactBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContactViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}