package com.cuongphan.vanastudio_v2.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cuongphan.vanastudio_v2.data_class.CartItemData
import com.cuongphan.vanastudio_v2.databinding.ItemCheckoutBinding

class CheckoutAdapter(
    private val items: List<CartItemData>
) : RecyclerView.Adapter<CheckoutAdapter.ViewHolder>(){

    inner class ViewHolder(val binding: ItemCheckoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemCheckoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ){
        val item = items[position]
        with(holder.binding) {
            Glide.with(checkoutItemPic.context).load(item.picUrl).into(checkoutItemPic)
            checkoutItemName.text = item.title
            if (item.color.isNullOrEmpty()){
                selectedColorLayout.visibility = View.GONE
                colorLabel.visibility = View.GONE
            }else{
                val colorInt = (item.color.replace("0x", "#") ?: "#FFFFFF").toColorInt()
                selectedColor.background.setTint(colorInt)
            }
            if (item.size.isNullOrEmpty()){
                checkoutSizeSelected.visibility = View.GONE
            }else{
                checkoutSizeSelected.text = "Size: ${item.size}"
            }
            checkoutAmount.text = "$%.2f".format(item.totalPrice())
            checkoutQuantity.text = item.quantity.toString()
        }
    }
}