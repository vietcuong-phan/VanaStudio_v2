package com.cuongphan.vanastudio_v2.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cuongphan.vanastudio_v2.databinding.ItemSizeBinding

class SizeAdapter(
    private val sizes: List<String>,
    private val onClick: (String) -> Unit
): RecyclerView.Adapter<SizeAdapter.SizeViewHolder>() {
    private var selectedPos = RecyclerView.NO_POSITION

    inner class SizeViewHolder(val binding: ItemSizeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(size: String, isSelected: Boolean) {
            binding.sizeTxt.text = size
            binding.sizeTxt.isSelected = isSelected

            if (isSelected) {
                binding.sizeTxt.setTextColor(Color.WHITE)
            } else {
                binding.sizeTxt.setTextColor(Color.BLACK)
            }

            binding.sizeTxt.setOnClickListener {
                val oldPos = selectedPos
                selectedPos = adapterPosition
                notifyItemChanged(oldPos)
                notifyItemChanged(selectedPos)
                onClick(size)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        SizeViewHolder(ItemSizeBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: SizeViewHolder, position: Int) =
        holder.bind(sizes[position], position == selectedPos)

    override fun getItemCount() = sizes.size
}