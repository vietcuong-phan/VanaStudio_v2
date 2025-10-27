package com.cuongphan.vanastudio_v2.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.cuongphan.vanastudio_v2.adapter.AddressCKAdapter
import com.cuongphan.vanastudio_v2.data_class.Address
import com.cuongphan.vanastudio_v2.databinding.ActivityChooseAddressBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChooseAddressActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChooseAddressBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var adapter: AddressCKAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid ?: return
        database = FirebaseDatabase.getInstance().getReference("Customers").child(uid).child("profile").child("address")

        binding.goAddNew.setOnClickListener { startActivity(
            Intent(
                this,
                AddAddressActivity::class.java
            )
        )}
        binding.gooBackBtnn.setOnClickListener { finish() }

        adapter = AddressCKAdapter(emptyList())
        binding.addressListRcv.apply {
            layoutManager = LinearLayoutManager(this@ChooseAddressActivity)
            adapter = this@ChooseAddressActivity.adapter
        }
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val addressList = mutableListOf<Address>()
                for (child in snapshot.children) {
                    val address = child.getValue(Address::class.java)
                    address?.let { addressList.add(it) }
                }
                adapter.updateList(addressList)
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        binding.applyBtnn.setOnClickListener {
            val selectedAddress = adapter.getSelectedAddress()
            if (selectedAddress != null) {
                val resultIntent = Intent()
                resultIntent.putExtra("selected_title", selectedAddress.title)
                resultIntent.putExtra("selected_address", selectedAddress.details)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            } else {
                Toast.makeText(this, "Please select an address!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}