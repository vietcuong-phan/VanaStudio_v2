package com.cuongphan.vanastudio_v2.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cuongphan.vanastudio_v2.data_class.ShippingTypeData
import com.cuongphan.vanastudio_v2.databinding.ItemShippingBinding

class ShippingCKAdapter(
    private var shippingList: List<ShippingTypeData>,
    private val onSelected: (ShippingTypeData) -> Unit
) : RecyclerView.Adapter<ShippingCKAdapter.ViewHolder>() {
    private var selectedPosition: Int = -1

    inner class ViewHolder(val binding: ItemShippingBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(shipping: ShippingTypeData, isSelected: Boolean){
            binding.titleTxt.text = shipping.title
            binding.descTxt.text = shipping.description
            binding.shipPriceTxt.text = "$${shipping.price}"
            binding.selectedBtnn.isChecked = position == selectedPosition
            Glide.with(binding.root.context).load(shipping.icon_url).into(binding.iconItem)
            binding.root.setOnClickListener {
                val previousPos = selectedPosition
                selectedPosition = adapterPosition
                notifyItemChanged(previousPos)
                notifyItemChanged(selectedPosition)
                onSelected(shipping)
            }
            binding.selectedBtnn.setOnClickListener {
                val previousPos = selectedPosition
                selectedPosition = adapterPosition
                notifyItemChanged(previousPos)
                notifyItemChanged(selectedPosition)
                onSelected(shipping)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemShippingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val item = shippingList[position]
        holder.bind(item, position == selectedPosition)
    }

    override fun getItemCount(): Int = shippingList.size
}