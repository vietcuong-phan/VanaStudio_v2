package com.cuongphan.vanastudio_v2.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cuongphan.vanastudio_v2.adapter.HelpCenterPagerAdapter
import com.cuongphan.vanastudio_v2.databinding.ActivityHelpCenterBinding
import com.google.android.material.tabs.TabLayoutMediator

class HelpCenterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHelpCenterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHelpCenterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = HelpCenterPagerAdapter(this)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "FAQ"
                1 -> tab.text = "Contact us"
            }
        }.attach()

        binding.imageView38.setOnClickListener { finish() }
    }
}