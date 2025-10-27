package com.cuongphan.vanastudio_v2.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.cuongphan.vanastudio_v2.adapter.OrderAdapter
import com.cuongphan.vanastudio_v2.data_class.OrderData
import com.cuongphan.vanastudio_v2.databinding.ActivityOrderBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OrderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderBinding
    private lateinit var orderAdapter: OrderAdapter
    private val orderList = mutableListOf<OrderData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupOrderList()
        loadOrders()

        //bottom menu setup:
        binding.orderHomeBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        binding.orderCartBtn.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
            finish()
        }
        binding.orderProfileBtn.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
            finish()
        }
    }

    private fun loadOrders() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val ordersRef = FirebaseDatabase.getInstance().getReference("Customers").child(userId).child("order")

        ordersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                orderList.clear()
                if (snapshot.exists()) {
                    for (orderSnap in snapshot.children) {
                        val order = orderSnap.getValue(OrderData::class.java)
                        order?.let { orderList.add(it) }
                    }
                }
                if (orderList.isEmpty()) {
                    binding.orderEmptyLayout.visibility = View.VISIBLE
                    binding.orderListLayout.visibility = View.GONE
                } else {
                    binding.orderEmptyLayout.visibility = View.GONE
                    binding.orderListLayout.visibility = View.VISIBLE
                }
                orderAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun setupOrderList() {
        orderAdapter = OrderAdapter(orderList)
        binding.orderListRcv.layoutManager = LinearLayoutManager(this)
        binding.orderListRcv.adapter = orderAdapter
    }
}