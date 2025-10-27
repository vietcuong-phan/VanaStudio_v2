package com.cuongphan.vanastudio_v2.activity

import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.webkit.WebSettings
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cuongphan.vanastudio_v2.data_class.Address
import com.cuongphan.vanastudio_v2.databinding.ActivityEditAddressBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EditAddressActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditAddressBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private var addressId: String? = null
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showMap()

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        userId = auth.currentUser?.uid ?: return
        addressId = intent.getStringExtra("addressId")

        if (addressId == null) {
            Toast.makeText(this, "Invalid address ID!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        showAddressInformation(addressId!!)

        binding.updateAddressDetailEdt.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)
            ) {
                val addressText = binding.updateAddressDetailEdt.text.toString().trim()
                if (addressText.isNotEmpty()) {
                    loadMap(addressText)
                }
                true
            } else {
                false
            }
        }

        binding.gooBackkBtn.setOnClickListener { finish() }
        binding.updateBtnn.setOnClickListener { updateAddressToDatabase() }
    }

    private fun showMap() {
        val webSettings: WebSettings = binding.maps.settings
        webSettings.javaScriptEnabled = true
        binding.maps.webViewClient = WebViewClient()
    }

    private fun updateAddressToDatabase() {
        val title = binding.updateAddressNameEdt.text.toString().trim()
        val addressTxt = binding.updateAddressDetailEdt.text.toString().trim()

        if (title.isEmpty() || addressTxt.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedAddress = Address(title, addressTxt)
        database.child("Customers").child(userId!!)
            .child("profile").child("address").child(addressId!!)
            .setValue(updatedAddress)
            .addOnSuccessListener {
                Toast.makeText(this, "Address updated successfully!", Toast.LENGTH_SHORT).show()
                finish()
                loadMap(addressTxt)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showAddressInformation(addressId: String) {
        database.child("Customers").child(userId!!)
            .child("profile").child("address").child(addressId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val address = snapshot.getValue(Address::class.java)
                    if (address != null) {
                        binding.updateAddressNameEdt.setText(address.title)
                        binding.updateAddressDetailEdt.setText(address.details)
                        loadMap(address.details)
                    } else {
                        Toast.makeText(
                            this@EditAddressActivity,
                            "Address not found.",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        this@EditAddressActivity,
                        "Failed to load address: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun loadMap(address: String) {
        val encodedAddress = java.net.URLEncoder.encode(address, "UTF-8")
        val mapUrl = "https://www.google.com/maps?q=$encodedAddress&output=embed"
        binding.maps.loadUrl(mapUrl)
    }
}