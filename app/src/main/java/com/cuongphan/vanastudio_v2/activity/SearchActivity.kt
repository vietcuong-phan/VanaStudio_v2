package com.cuongphan.vanastudio_v2.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.transition.TransitionInflater
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.cuongphan.vanastudio_v2.adapter.ProductAdapter
import com.cuongphan.vanastudio_v2.data_class.CategoryData
import com.cuongphan.vanastudio_v2.data_class.ProductData
import com.cuongphan.vanastudio_v2.databinding.ActivitySearchBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private lateinit var productAdapter: ProductAdapter
    private lateinit var productRef: DatabaseReference
    private lateinit var categoryRef: DatabaseReference

    private val productList = mutableListOf<ProductData>()
    private val categoryList = mutableListOf<CategoryData>()
    private var isProductLoaded = false
    private var isCategoryLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.sharedElementEnterTransition =
            TransitionInflater.from(this).inflateTransition(android.R.transition.move)
        window.sharedElementExitTransition =
            TransitionInflater.from(this).inflateTransition(android.R.transition.move)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupResult()
        setupConnectFirebase()
        loadCategories()
        loadProducts()
        setupSearchListener()

        binding.goBackk.setOnClickListener { finish() }
    }

    private fun setupSearchListener() {
        binding.searchEdt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val keyword = s.toString().trim()

                if (!isProductLoaded || !isCategoryLoaded) {
                    Log.d("SearchDebug", "Data not ready yet.")
                    return
                }
                if (keyword.isEmpty()) {
                    productAdapter.setItems(emptyList())
                    binding.resultLayout.visibility = View.GONE
                    binding.noResultLayout.visibility = View.GONE
                    return
                }

                loadProducts()
                loadCategories()
                binding.resultRcv.postDelayed({
                    searchProducts(keyword)
                }, 100)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun searchProducts(keyword: String) {
        val normalizedKeyword = keyword.trim().lowercase()
        val foundByName = productList.filter { product ->
            product.title?.trim()?.lowercase()?.contains(normalizedKeyword) == true
        }
        val matchedCategories = categoryList.filter { category ->
            category.title.trim().lowercase().contains(normalizedKeyword)
        }
        val foundByCategory = if (matchedCategories.isNotEmpty()) {
            productList.filter { product ->
                matchedCategories.any { cat ->
                    product.category_id?.toLong() == cat.id.toLong()
                }
            }
        } else emptyList()

        val finalResults = (foundByName + foundByCategory).distinctBy { it.id }

        if (finalResults.isNotEmpty()) {
            binding.resultLayout.visibility = View.VISIBLE
            binding.noResultLayout.visibility = View.GONE
            binding.resultLabel.text = "Results for \"$keyword\""
            binding.foundResult.text = "${finalResults.size} found(s)"
            productAdapter.setItems(finalResults)
        } else {
            binding.resultLayout.visibility = View.GONE
            binding.noResultLayout.visibility = View.VISIBLE
            binding.resultLabelTxt.text = "Results for \"$keyword\""
            binding.foundResultTxt.text = "0 found"
            productAdapter.setItems(emptyList())
        }
    }

    private fun setupConnectFirebase() {
        productRef = FirebaseDatabase.getInstance().getReference("Products")
        categoryRef = FirebaseDatabase.getInstance().getReference("Categories")
    }

    private fun setupResult() {
        productAdapter = ProductAdapter(productList,
            onItemClick = { product ->
                val intent = Intent(this, DetailActivity::class.java)
                intent.putExtra("product", product)
                startActivity(intent)
            }
        )
        binding.resultRcv.layoutManager = GridLayoutManager(this, 2)
        binding.resultRcv.adapter = productAdapter
    }

    private fun loadProducts(forceReload: Boolean = false) {
        if (productList.isNotEmpty() && !forceReload) {
            isProductLoaded = true
            return
        }

        productRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productList.clear()
                for (child in snapshot.children) {
                    val product = child.getValue(ProductData::class.java)
                    product?.let { productList.add(it) }
                }
                isProductLoaded = true
                Log.d("Firebase", "Loaded ${productList.size} products")
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun loadCategories(forceReload: Boolean = false) {
        if (categoryList.isNotEmpty() && !forceReload) {
            isCategoryLoaded = true
            return
        }

        categoryRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                categoryList.clear()
                for (child in snapshot.children) {
                    val category = child.getValue(CategoryData::class.java)
                    category?.let { categoryList.add(it) }
                }
                isCategoryLoaded = true
                Log.d("Firebase", "Loaded ${categoryList.size} categories")
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}