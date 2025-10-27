package com.cuongphan.vanastudio_v2.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.cuongphan.vanastudio_v2.adapter.WishlistAdapter
import com.cuongphan.vanastudio_v2.databinding.ActivityFavoriteBinding
import com.cuongphan.vanastudio_v2.manager.WishlistManager

class FavoriteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavoriteBinding
    private lateinit var wishlistAdapter: WishlistAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.goPrevBtn.setOnClickListener { finish() }

        binding.emptyWishlist.visibility = View.GONE
        binding.showWishlist.visibility = View.VISIBLE

        wishlistAdapter = WishlistAdapter(
            onItemClick = { product ->
                val intent = Intent(this, DetailActivity::class.java)
                intent.putExtra("product", product)
                startActivity(intent)
            },
            onFavClick = { product, position ->
                wishlistAdapter.removeItemAt(position)
            }
        )
        binding.wishlistRcv.layoutManager = GridLayoutManager(this, 2)
        binding.wishlistRcv.adapter = wishlistAdapter
        wishlistAdapter.setItems(WishlistManager.getWishlist())
    }
}