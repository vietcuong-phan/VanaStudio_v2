package com.cuongphan.vanastudio_v2.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.cuongphan.vanastudio_v2.adapter.CheckoutAdapter
import com.cuongphan.vanastudio_v2.data_class.Address
import com.cuongphan.vanastudio_v2.databinding.ActivityCheckoutBinding
import com.cuongphan.vanastudio_v2.manager.CartManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class CheckoutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCheckoutBinding
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val chooseAddressLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val title = result.data!!.getStringExtra("selected_title")
                val address = result.data!!.getStringExtra("selected_address")
                binding.addressNameTxt.text = title
                binding.shipAddress.text = address
            }
        }
    private val chooseShipTypeLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val sTitle = result.data!!.getStringExtra("selected_ship_title")
                val sDesc = result.data!!.getStringExtra("selected_ship_desc")
                val sShipPrice = result.data!!.getStringExtra("selected_ship_price")
                val sIconUrl = result.data!!.getStringExtra("selected_ship_icon")
                val sPriceValue = result.data!!.getDoubleExtra("shipping", 0.0)

                binding.chooseShippingBtn.visibility = View.GONE
                binding.shippingTypeSelectedLayout.visibility = View.VISIBLE
                binding.shipTitle.text = sTitle
                binding.shipDesc.text = sDesc
                binding.shipPrice.text = sShipPrice
                Glide.with(this).load(sIconUrl).into(binding.shipIcon)

                updateSummary(sPriceValue)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.amountTxt.text = "$%.2f".format(CartManager.cartItems.sumOf { it.totalPrice() })
        binding.taxTxt.text = "$%.2f".format(CartManager.cartItems.sumOf { it.totalPrice() } * 0.05)
        binding.shippingTxt.text = "---"
        binding.totalTxt.text = "---"

        setUpListOrder()
        getAddress()
        getShippingType()

        binding.coBackBtn.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
        binding.gotoPaymentBtn.setOnClickListener {
            if (binding.shippingTypeSelectedLayout.visibility != View.VISIBLE){
                Toast.makeText(this, "Please choose shipping type!", Toast.LENGTH_SHORT).show()
            }else{
                startActivity(Intent(this, PaymentActivity::class.java))
            }
        }
        binding.chooseOthersBtn.setOnClickListener {
            val intent = Intent(this, ChooseAddressActivity::class.java)
            chooseAddressLauncher.launch(intent)
        }
        binding.chooseShippingBtn.setOnClickListener {
            val intent = Intent(this, ChooseShippingActivity::class.java)
            chooseShipTypeLauncher.launch(intent)
        }
        binding.chooseShipType.setOnClickListener {
            val intent = Intent(this, ChooseShippingActivity::class.java)
            chooseShipTypeLauncher.launch(intent)
        }
    }

    private fun getAddress() {
        val uid = auth.currentUser?.uid
        val addressRef = FirebaseDatabase.getInstance().getReference("Customers")
            .child(uid!!).child("profile").child("address").limitToFirst(1)
        addressRef.get().addOnSuccessListener {snapshot ->
            if (snapshot.exists()) {
                val firstAddressSnapshot = snapshot.children.first()
                val firstAddress = firstAddressSnapshot.getValue(Address::class.java)
                if (firstAddress != null) {
                    binding.addressNameTxt.text = firstAddress.title
                    binding.shipAddress.text = firstAddress.details
                }
            } else {
                binding.addressNameTxt.text = "No address"
                binding.shipAddress.text = "No location"
            }
        }
    }

    private fun getShippingType() {
        FirebaseDatabase.getInstance().getReference("ShippingTypes")
            .get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    binding.chooseShippingBtn.visibility = View.VISIBLE
                    binding.shippingTypeSelectedLayout.visibility = View.GONE
                } else {
                    binding.chooseShippingBtn.visibility = View.GONE
                    binding.shippingTypeSelectedLayout.visibility = View.VISIBLE
                    updateSummary(0.0)
                }
            }
    }

    private fun updateSummary(shipping: Double) {
        val subtotal = CartManager.cartItems.sumOf { it.totalPrice() }
        val tax = subtotal * 0.05
        val total = subtotal + shipping + tax

        binding.amountTxt.text = "$%.2f".format(subtotal)
        binding.taxTxt.text = "$%.2f".format(tax)
        binding.shippingTxt.text = "$%.2f".format(shipping)
        binding.totalTxt.text = "$%.2f".format(total)

        CartManager.updateTotal(this, total)
    }

    private fun setUpListOrder() {
        binding.orderListRcv.layoutManager = LinearLayoutManager(this)
        binding.orderListRcv.adapter = CheckoutAdapter(CartManager.cartItems)
    }
}