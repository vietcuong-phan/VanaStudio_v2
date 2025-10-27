package com.cuongphan.vanastudio_v2.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cuongphan.vanastudio_v2.R
import com.cuongphan.vanastudio_v2.databinding.ItemColorBinding

class ColorAdapter(
    private val colors: List<String>,
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<ColorAdapter.ColorViewHolder>() {
    private var selectedPos = RecyclerView.NO_POSITION

    inner class ColorViewHolder(val binding: ItemColorBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(colorHex: String, isSelected: Boolean) {
            val colorInt = Color.parseColor(colorHex.replace("0x", "#"))
            binding.innerCircle.background.setTint(colorInt)
            if (isSelected) {
                binding.outerCircle.setBackgroundResource(R.drawable.color_outer_selected)
            } else {
                binding.outerCircle.setBackgroundResource(R.drawable.color_outer_bg)
            }

            binding.root.setOnClickListener {
                val oldPos = selectedPos
                selectedPos = adapterPosition
                notifyItemChanged(oldPos)
                notifyItemChanged(selectedPos)
                onClick(colorHex)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ColorViewHolder(ItemColorBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) =
        holder.bind(colors[position], position == selectedPos)

    override fun getItemCount() = colors.size
}