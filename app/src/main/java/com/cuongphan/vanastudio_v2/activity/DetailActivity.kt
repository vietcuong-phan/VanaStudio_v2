package com.cuongphan.vanastudio_v2.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.cuongphan.vanastudio_v2.adapter.ColorAdapter
import com.cuongphan.vanastudio_v2.adapter.SizeAdapter
import com.cuongphan.vanastudio_v2.data_class.CartItemData
import com.cuongphan.vanastudio_v2.data_class.ProductData
import com.cuongphan.vanastudio_v2.databinding.ActivityDetailBinding
import com.cuongphan.vanastudio_v2.manager.CartManager
import com.cuongphan.vanastudio_v2.manager.WishlistManager

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private var selectedSize: String? = null
    private var selectedColor: String? = null
    private var quantity: Int = 1
    private var unitPrice: Double = 0.0
    private var totalPrice: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backBtn.setOnClickListener { finish() }

        // display product details
        val product = intent.getSerializableExtra("product") as? ProductData
        unitPrice = product?.price ?: 0.0

        product?.let {
            val pic = binding.detailPic
            Glide.with(this).load(it.picUrl).into(pic)
            binding.detailProductTitle.text = it.title ?: "No title"
            binding.detailSoldTxt.text = "${it.sold} solds" ?: "0 sold"
            binding.detailReviewTxt.text = "(${it.review} reviews)"
            binding.detailRatingTxt.text = it.rating.toString() ?: "0.0"
            binding.detailDescriptionTxt.text = it.description ?: "No description"
            binding.detailTotalTxt

            if (product.size.isNullOrEmpty()) {
                binding.sizeLayout.visibility = View.GONE
            } else {
                val sizeAdapter = SizeAdapter(product.size!!) { size ->
                    selectedSize = size
                }
                binding.sizeRcv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                binding.sizeRcv.adapter = sizeAdapter
            }
            if (product.color.isNullOrEmpty()) {
                binding.colorLayout.visibility = View.GONE
            } else {
                val colorAdapter = ColorAdapter(product.color!!) { colorHex ->
                    selectedColor = colorHex
                }
                binding.colorRcv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                binding.colorRcv.adapter = colorAdapter
            }

            if (WishlistManager.isInWishlist(product)) {
                binding.favUnselectedBtn.visibility = View.GONE
                binding.favSelectedBtn.visibility = View.VISIBLE
            } else {
                binding.favUnselectedBtn.visibility = View.VISIBLE
                binding.favSelectedBtn.visibility = View.GONE
            }

            binding.favUnselectedBtn.setOnClickListener {
                WishlistManager.addToWishlist(product)
                binding.favUnselectedBtn.visibility = View.GONE
                binding.favSelectedBtn.visibility = View.VISIBLE
                Toast.makeText(this, "Added to wishlist", Toast.LENGTH_SHORT).show()
            }
            binding.favSelectedBtn.setOnClickListener {
                WishlistManager.removeFromWishlist(product)
                binding.favUnselectedBtn.visibility = View.VISIBLE
                binding.favSelectedBtn.visibility = View.GONE
                Toast.makeText(this, "Removed from wishlist", Toast.LENGTH_SHORT).show()
            }
        }

        binding.detailQuantity.text = quantity.toString()
        updateTotalPrice()

        binding.detailPlus.setOnClickListener {
            quantity++
            binding.detailQuantity.text = quantity.toString()
            updateTotalPrice()
        }
        binding.detailMinus.setOnClickListener {
            if (quantity > 1) {
                quantity--
                binding.detailQuantity.text = quantity.toString()
                updateTotalPrice()
            }
        }

        binding.addcartBtn.setOnClickListener {
            if (!product?.size.isNullOrEmpty() && selectedSize == null) {
                Toast.makeText(this, "Please choose size of item!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!product?.color.isNullOrEmpty() && selectedColor == null) {
                Toast.makeText(this, "Please choose color of item!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val cartItem = CartItemData(
                picUrl = product?.picUrl ?: "",
                title = product?.title ?: "",
                color = selectedColor,
                size = selectedSize,
                quantity = quantity,
                unitPrice = unitPrice
            )
            CartManager.addItem(this, cartItem)
            Toast.makeText(this, "Add to cart successfully!", Toast.LENGTH_SHORT).show()
        }

        binding.goToCartBtn.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
    }

    private fun updateTotalPrice() {
        totalPrice = quantity * unitPrice
        binding.detailTotalTxt.text = String.format("$%.2f", totalPrice)
    }
}