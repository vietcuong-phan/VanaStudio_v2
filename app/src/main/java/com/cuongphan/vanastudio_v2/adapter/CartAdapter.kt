package com.cuongphan.vanastudio.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import androidx.core.graphics.toColorInt
import com.cuongphan.vanastudio_v2.data_class.CartItemData
import com.cuongphan.vanastudio_v2.databinding.DialogConfirmRemoveBinding
import com.cuongphan.vanastudio_v2.databinding.ItemCartListBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class CartAdapter(
    private val items: MutableList<CartItemData>,
    private val onUpdate: () -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(val binding: ItemCartListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CartItemData) {
            if (item.color.isNullOrEmpty()) {
                binding.selectedColorLayout.visibility = View.GONE
                binding.colorLabel.visibility = View.GONE
            }else{
                val colorInt = (item.color.replace("0x", "#") ?: "#FFFFFF").toColorInt()
                binding.selectedColor.background.setTint(colorInt)
            }
            if (item.size.isNullOrEmpty()){
                binding.cartSizeSelected.visibility = View.GONE
            }else{
                binding.cartSizeSelected.text = "Size: ${item.size}"
            }
            binding.cartItemName.text = item.title
            binding.cartAmount.text = String.format("$%.2f", item.totalPrice())
            binding.cartQuantity.text = item.quantity.toString()
            Glide.with(binding.root.context).load(item.picUrl).into(binding.cartItemPic)

            binding.cartPlusBtn.setOnClickListener {
                item.quantity++
                notifyItemChanged(adapterPosition)
                onUpdate()
            }
            binding.cartMinusBtn.setOnClickListener {
                if (item.quantity > 1) {
                    item.quantity--
                    notifyItemChanged(adapterPosition)
                    onUpdate()
                }
            }
            binding.removeBtn.setOnClickListener {
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    showRemoveDialog(binding.root.context, item, pos)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        CartViewHolder(ItemCartListBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) =   holder.bind(items[position])

    override fun getItemCount() = items.size

    private fun showRemoveDialog(context: android.content.Context, item: CartItemData, position: Int) {
        val dialogBinding = DialogConfirmRemoveBinding.inflate(LayoutInflater.from(context))
        val dialog = BottomSheetDialog(context)
        dialog.setContentView(dialogBinding.root)

        // Gán dữ liệu vào dialog
        dialogBinding.cartItemName.text = item.title
        dialogBinding.cartQuantity.text = item.quantity.toString()
        dialogBinding.cartSizeSelected.text = if (item.size.isNullOrEmpty()) "" else "Size: ${item.size}"
        dialogBinding.cartAmount.text = String.format("$%.2f", item.totalPrice())

        if (item.color.isNullOrEmpty()) {
            dialogBinding.selectedColorLayout.visibility = View.GONE
            dialogBinding.colorLabel.visibility = View.GONE
        } else {
            val colorInt = (item.color.replace("0x", "#") ?: "#FFFFFF").toColorInt()
            dialogBinding.selectedColor.background.setTint(colorInt)
        }

        Glide.with(context).load(item.picUrl).into(dialogBinding.cartItemPic)

        // Nút Cancel
        dialogBinding.cancelBtn.setOnClickListener {
            dialog.dismiss()
        }

        // Nút Yes, remove
        dialogBinding.yesBtn.setOnClickListener {
            items.removeAt(position)
            notifyItemRemoved(position)
            onUpdate()
            dialog.dismiss()
        }

        dialog.show()
    }
}