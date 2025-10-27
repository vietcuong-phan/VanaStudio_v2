package com.cuongphan.vanastudio_v2.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.cuongphan.vanastudio.adapter.CartAdapter
import com.cuongphan.vanastudio_v2.databinding.ActivityCartBinding
import com.cuongphan.vanastudio_v2.manager.CartManager
import com.google.firebase.auth.FirebaseAuth

class CartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCartBinding
    private lateinit var cartAdapter: CartAdapter
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        CartManager.init(this)
        cartAdapter = CartAdapter(CartManager.cartItems) {
            CartManager.save(this)
            updateCartUI()
        }
        binding.cartListRcv.layoutManager = LinearLayoutManager(this)
        binding.cartListRcv.adapter = cartAdapter

        updateCartUI()

        binding.goToCheckoutBtn.setOnClickListener {
            handleCheckout()
        }
        binding.cartHomeBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        binding.cartProfileBtn.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
            finish()
        }
    }

    private fun handleCheckout() {
        val user = auth.currentUser
        if (user == null) {
            val intent = Intent(this, SigninPasswordActivity::class.java).apply {
                putExtra("redirectToCheckout", true)
            }
            startActivity(intent)
        }else {
            startActivity(Intent(this, CheckoutActivity::class.java))
        }
    }

    private fun updateCartUI() {
        if (CartManager.cartItems.isEmpty()) {
            binding.emptyCartLayout.visibility = View.VISIBLE
            binding.cartListItemLayout.visibility = View.GONE
            binding.continueLayout.visibility = View.GONE
        } else {
            binding.emptyCartLayout.visibility = View.GONE
            binding.cartListItemLayout.visibility = View.VISIBLE
            binding.continueLayout.visibility = View.VISIBLE

            val total = CartManager.cartItems.sumOf { it.totalPrice() }
            binding.cartTotalPriceTxt.text = String.format("$%.2f", total)
        }
    }
}