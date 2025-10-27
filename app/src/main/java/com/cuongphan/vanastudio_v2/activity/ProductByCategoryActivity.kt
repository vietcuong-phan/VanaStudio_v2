package com.cuongphan.vanastudio_v2.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.cuongphan.vanastudio_v2.adapter.ProductAdapter
import com.cuongphan.vanastudio_v2.data_class.ProductData
import com.cuongphan.vanastudio_v2.databinding.ActivityProductByCategoryBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProductByCategoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductByCategoryBinding
    private lateinit var database: DatabaseReference
    private lateinit var adapter: ProductAdapter
    private val productList = mutableListOf<ProductData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductByCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtn.setOnClickListener { finish() }

        val categoryId = intent.getIntExtra("categoryId", -1)
        val categoryTitle = intent.getStringExtra("categoryTitle") ?: "Products"
        binding.listPrb.visibility = View.GONE
        binding.categoryTitleTxt.text = categoryTitle
        binding.categoryTitleTxt.textSize = 22f

        adapter = ProductAdapter(productList,
            onItemClick = { product ->
                val intent = Intent(this, DetailActivity::class.java)
                intent.putExtra("product", product)
                startActivity(intent)
            }
        )
        binding.listRcv.layoutManager = GridLayoutManager(this, 2)
        binding.listRcv.adapter = adapter

        database = FirebaseDatabase.getInstance().getReference("Products")
        database.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productList.clear()
                for (data in snapshot.children) {
                    val product = data.getValue(ProductData::class.java)
                    if (product?.category_id == categoryId) {
                        productList.add(product)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ProductByCategoryActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}