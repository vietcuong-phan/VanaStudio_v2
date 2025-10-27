package com.cuongphan.vanastudio_v2.activity

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.cuongphan.vanastudio_v2.adapter.CategoryAdapter
import com.cuongphan.vanastudio_v2.adapter.ProductAdapter
import com.cuongphan.vanastudio_v2.data_class.CategoryData
import com.cuongphan.vanastudio_v2.data_class.ProductData
import com.cuongphan.vanastudio_v2.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var database: DatabaseReference
    private lateinit var categoryList: MutableList<CategoryData>
    private lateinit var adapter: CategoryAdapter
    private val productList = mutableListOf<ProductData>()
    private lateinit var adapter2: ProductAdapter
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            // get customer's name
            val displayName = FirebaseDatabase.getInstance().getReference("Customers").child(uid!!).child("profile").child("name")
            displayName.get().addOnCompleteListener { snapshot ->
                val name = snapshot.result.getValue(String::class.java)
                binding.nameTxt.text = name
            }
            // get customer's avatar
            val avatarRef = FirebaseDatabase.getInstance().getReference("Customers").child(uid).child("profile").child("avatar")
            avatarRef.get().addOnCompleteListener { snapshot ->
                val avatarUrl = snapshot.result.getValue(String::class.java)
                Glide.with(this).load(avatarUrl).into(binding.avatar)
            }
        }else{
            val img_url = "https://www.pngall.com/wp-content/uploads/5/User-Profile-PNG.png"
            Glide.with(this).load(img_url).into(binding.avatar)
            binding.nameTxt.text = "Guest."
        }

        binding.seeAllOfferTxt.setOnClickListener { startActivity(Intent(this, AllOfferActivity::class.java)) }

        binding.favoriteBtn.setOnClickListener { startActivity(Intent(this, FavoriteActivity::class.java)) }

        // setup for category
        binding.categoryRcv.layoutManager = GridLayoutManager(this, 4)
        binding.categoryPrb.visibility = View.GONE
        categoryList = mutableListOf()
        adapter = CategoryAdapter(categoryList){category ->
            val intent = Intent(this, ProductByCategoryActivity::class.java)
            intent.putExtra("categoryId", category.id)
            intent.putExtra("categoryTitle", category.title)
            startActivity(intent)
        }
        binding.categoryRcv.adapter = adapter
        database = FirebaseDatabase.getInstance().getReference("Categories")
        database.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                categoryList.clear()
                for (data in snapshot.children) {
                    val category = data.getValue(CategoryData::class.java)
                    if (category != null) {
                        categoryList.add(category)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })

        // setup for recommend
        adapter2 = ProductAdapter(productList,
            onItemClick = { product ->
                val intent = Intent(this, DetailActivity::class.java)
                intent.putExtra("product", product)
                startActivity(intent)
            }
        )
        binding.recommendPrb.visibility = View.GONE
        binding.recommendRcv.layoutManager = GridLayoutManager(this, 2)
        binding.recommendRcv.adapter = adapter2
        database = FirebaseDatabase.getInstance().getReference("Products")
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productList.clear()
                for (data in snapshot.children) {
                    val product = data.getValue(ProductData::class.java)
                    if (product?.recommend == true) {
                        productList.add(product)
                    }
                }
                adapter2.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })

        // setup clickable for bottom menu
        binding.mainCartBtn.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.mainProfileBtn.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
            finish()
        }

        // search:
        binding.goSearchBtn.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            val options = ActivityOptions.makeSceneTransitionAnimation(
                this,
                binding.goSearchBtn,
                "searchBar"
            )
            startActivity(intent, options.toBundle())
        }
    }

    override fun onResume() {
        super.onResume()
        adapter2.notifyDataSetChanged()
    }
}