package com.cuongphan.vanastudio_v2.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cuongphan.vanastudio_v2.data_class.OrderData
import com.cuongphan.vanastudio_v2.databinding.ActivityPaymentBinding
import com.cuongphan.vanastudio_v2.databinding.DialogOrderSuccessBinding
import com.cuongphan.vanastudio_v2.manager.CartManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class PaymentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPaymentBinding
    private var selectedMethod: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRadioButtons()
        setupConfirmButton()

        binding.pmBack.setOnClickListener { finish() }
    }

    private fun setupRadioButtons() {
        binding.paypalRB.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedMethod = "Paypal"
                if (binding.codRB.isChecked) binding.codRB.isChecked = false
            }
        }
        binding.codRB.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedMethod = "COD"
                if (binding.paypalRB.isChecked) binding.paypalRB.isChecked = false
            }
        }
    }

    private fun setupConfirmButton() {
        binding.confirmPaymentBtn.setOnClickListener {
            if (selectedMethod == null) {
                Toast.makeText(this, "Please select a payment method!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            when (selectedMethod) {
                "COD" -> {
                    saveOrderToDatabase("CoD")
                    showOrderSuccessDialog()
                }
                "Paypal" -> {
                    saveOrderToDatabase("Paypal")
                    showOrderSuccessDialog()
                }
            }
        }
    }

    private fun saveOrderToDatabase(paymentMethod: String) {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val cartItems = CartManager.cartItems
        val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
        val currentDate = sdf.format(Date())
        val database = FirebaseDatabase.getInstance()
        val ordersRef = database.getReference("Customers").child(userId).child("order")

        cartItems.forEach { item ->
            val orderId = ordersRef.push().key ?: return@forEach
            val order = OrderData(
                orderId = orderId,
                productName = item.title,
                price = item.unitPrice*item.quantity,
                quantity = item.quantity,
                size = item.size,
                color = item.color,
                paymentMethod = paymentMethod,
                orderDate = currentDate,
                productImage = item.picUrl
            )
            ordersRef.child(orderId).setValue(order)
        }
        CartManager.clear(this)
    }

    private fun showOrderSuccessDialog() {
        val dialogBinding = DialogOrderSuccessBinding.inflate(layoutInflater)
        val dialog = android.app.AlertDialog.Builder(this).create()
        dialog.setView(dialogBinding.root)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialogBinding.btnViewOrder.setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(this, OrderActivity::class.java))
            finish()
        }
        dialog.show()
    }
}