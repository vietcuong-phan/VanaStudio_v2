package com.cuongphan.vanastudio_v2.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.cuongphan.vanastudio_v2.adapter.ShippingCKAdapter
import com.cuongphan.vanastudio_v2.data_class.ShippingTypeData
import com.cuongphan.vanastudio_v2.databinding.ActivityChooseShippingBinding
import com.google.firebase.database.FirebaseDatabase

class ChooseShippingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChooseShippingBinding
    private var selectedShipping: ShippingTypeData?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseShippingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val ref = FirebaseDatabase.getInstance().getReference("ShippingTypes")
        ref.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val shippingList = mutableListOf<ShippingTypeData>()
                for (child in snapshot.children) {
                    val ship = child.getValue(ShippingTypeData::class.java)
                    if (ship != null) shippingList.add(ship)
                }

                val adapter = ShippingCKAdapter(shippingList) { selected ->
                    selectedShipping = selected
                }
                binding.shippingListRcv.layoutManager = LinearLayoutManager(this)
                binding.shippingListRcv.adapter = adapter
            }
        }.addOnFailureListener { it.printStackTrace() }

        binding.goApplyBtn.setOnClickListener {
            selectedShipping?.let { ship ->
                val data = Intent().apply {
                    putExtra("selected_ship_title", ship.title)
                    putExtra("selected_ship_desc", ship.description)
                    putExtra("selected_ship_price", "$${ship.price}")
                    putExtra("selected_ship_icon", ship.icon_url)
                    putExtra("shipping", ship.price)
                }
                setResult(RESULT_OK, data)
                finish()
            }
        }

        binding.gooBackBtnn.setOnClickListener { finish() }
    }
}