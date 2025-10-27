package com.cuongphan.vanastudio_v2.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cuongphan.vanastudio_v2.data_class.OrderData
import com.cuongphan.vanastudio_v2.databinding.ItemOrderBinding

class OrderAdapter(
    private val items: MutableList<OrderData>
) : RecyclerView.Adapter<OrderAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemOrderBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val item = items[position]
        with(holder.binding) {
            Glide.with(orderPrice.context).load(item.productImage).into(orderPic)
            orderProductName.text = item.productName
            orderPM.text = item.paymentMethod
            if (item.color.isNullOrEmpty()){
                selectedColorLayout.visibility = View.GONE
                colorLabel2.visibility = View.GONE
            }else{
                val colorInt = (item.color.replace("0x", "#") ?: "#FFFFFF").toColorInt()
                selectColor.background.setTint(colorInt)
            }
            if (item.size.isNullOrEmpty()){
                orderSize.visibility = View.GONE
            }else{
                orderSize.text = "Size: ${item.size}"
            }
            orderDate.text = "Order at: ${item.orderDate}"
            orderPrice.text = "$%.2f".format(item.price)
            orderQuantity.text = "Qty: ${item.quantity.toString()}"
        }
    }

    override fun getItemCount(): Int = items.size
}