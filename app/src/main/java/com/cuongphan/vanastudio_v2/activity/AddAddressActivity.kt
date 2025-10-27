package com.cuongphan.vanastudio_v2.activity

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cuongphan.vanastudio_v2.data_class.Address
import com.cuongphan.vanastudio_v2.databinding.ActivityAddAddressBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.net.URLEncoder

class AddAddressActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddAddressBinding
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        database = FirebaseDatabase.getInstance()

        // Webview configuration
        binding.maps.settings.javaScriptEnabled = true
        binding.maps.webViewClient = WebViewClient()
        binding.maps.loadUrl("https://www.google.com/maps/@14.0583,108.2772,6z") // default location in Vietnam
        binding.detailAddressEdt.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH) {
                val address = binding.detailAddressEdt.text.toString().trim()
                if (address.isNotEmpty()) {
                    showAddressOnMap(address)
                } else {
                    Toast.makeText(this, "Please enter an address!", Toast.LENGTH_SHORT).show()
                }
                true
            } else false
        }

        binding.gooBackkBtn.setOnClickListener { finish() }
        binding.addBtnn.setOnClickListener { addNewAddress() }
    }

    private fun showAddressOnMap(address: String) {
        try {
            val encodedAddress = URLEncoder.encode(address, "UTF-8")
            val mapUrl = "https://www.google.com/maps/search/?api=1&query=$encodedAddress"
            binding.maps.loadUrl(mapUrl)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun addNewAddress() {
        val title = binding.addressNameEdt.text.toString().trim()
        val addressTxt = binding.detailAddressEdt.text.toString().trim()

        if (title.isEmpty() || addressTxt.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields!", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val newAddress = Address(
            title = title,
            details = addressTxt
        )

        database.getReference("Customers").child(userId!!).child("profile").child("address").push().setValue(newAddress)
            .addOnSuccessListener {
                Toast.makeText(this, "Add new address successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Add new address failed! Try again.", Toast.LENGTH_SHORT).show()
            }
    }
}