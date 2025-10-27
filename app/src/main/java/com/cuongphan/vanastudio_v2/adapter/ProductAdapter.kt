package com.cuongphan.vanastudio_v2.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cuongphan.vanastudio_v2.manager.WishlistManager
import com.cuongphan.vanastudio_v2.data_class.ProductData
import com.cuongphan.vanastudio_v2.databinding.ItemProductBinding

class ProductAdapter(
    private val products: MutableList<ProductData> = mutableListOf(),
    private val onItemClick: (ProductData) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(val binding: ItemProductBinding)
        : RecyclerView.ViewHolder(binding.root){
        fun bind(product: ProductData){
            Glide.with(binding.root.context).load(product.picUrl).into(binding.productImage)
            binding.root.setOnClickListener {
                onItemClick(product)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ProductViewHolder,
        position: Int
    ) {
        val product = products[position]
        with(holder.binding) {
            productTitle.text = product.title
            productRating.text = product.rating?.toString() ?: "0.0"
            productSold.text = "${product.sold ?: 0} sold"
            productPrice.text = "$${product.price ?: 0.0}"

            Glide.with(root.context)
                .load(product.picUrl)
                .into(productImage)

            if (WishlistManager.isInWishlist(product)) {
                selectFavBtn.visibility = View.VISIBLE
                unselectFavBtn.visibility = View.GONE
            } else {
                selectFavBtn.visibility = View.GONE
                unselectFavBtn.visibility = View.VISIBLE
            }

            unselectFavBtn.setOnClickListener {
                WishlistManager.addToWishlist(product)
                notifyItemChanged(position)
                Toast.makeText(root.context, "Added to wishlist", Toast.LENGTH_SHORT).show()
            }
            selectFavBtn.setOnClickListener {
                WishlistManager.removeFromWishlist(product)
                notifyItemChanged(position)
                Toast.makeText(root.context, "Removed from wishlist", Toast.LENGTH_SHORT).show()
            }

            holder.bind(products[position])
        }
    }

    override fun getItemCount() = products.size

    fun setItems(newItems: List<ProductData>) {
        products.clear()
        products.addAll(newItems)
        notifyDataSetChanged()
    }
}