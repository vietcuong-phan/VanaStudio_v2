package com.cuongphan.vanastudio_v2.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cuongphan.vanastudio_v2.data_class.Address
import com.cuongphan.vanastudio_v2.databinding.ItemAddressCkBinding

class AddressCKAdapter(
    private var addressList: List<Address>
): RecyclerView.Adapter<AddressCKAdapter.ViewHolder>() {
    private var selectedPosition: Int = -1

    inner class ViewHolder(val binding: ItemAddressCkBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(address: Address){
            binding.titleTxt.text = address.title
            binding.addressTxt.text = address.details
            binding.selectedBtnn.isChecked = position == selectedPosition
            binding.selectedBtnn.setOnClickListener {
                selectedPosition = position
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemAddressCkBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bind(addressList[position])
    }

    override fun getItemCount(): Int = addressList.size

    fun updateList(newList: List<Address>) {
        addressList = newList
        notifyDataSetChanged()
    }

    fun getSelectedAddress(): Address? {
        return if (selectedPosition != -1) addressList[selectedPosition] else null
    }
}