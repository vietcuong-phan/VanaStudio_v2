package com.cuongphan.vanastudio_v2.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cuongphan.vanastudio_v2.data_class.Address
import com.cuongphan.vanastudio_v2.databinding.ItemAddressBinding

class AddressAdapter(
    private var addressList: List<Address>,
    private val onItemClick: (Address) -> Unit
): RecyclerView.Adapter<AddressAdapter.ViewHolder>() {
    val currentList: List<Address>
        get() = addressList

    inner class ViewHolder(val binding: ItemAddressBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(address: Address){
            binding.titleTxt.text = address.title
            binding.addressTxt.text = address.details
            binding.editAddressBtn.setOnClickListener { onItemClick.invoke(address) }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemAddressBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    fun removeItem(position: Int) {
        if (position in addressList.indices) {
            addressList = addressList.toMutableList().apply { removeAt(position) }
            notifyItemRemoved(position)
        }
    }
}