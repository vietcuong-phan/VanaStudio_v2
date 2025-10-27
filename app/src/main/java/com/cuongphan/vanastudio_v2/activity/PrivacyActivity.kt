package com.cuongphan.vanastudio_v2.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cuongphan.vanastudio_v2.databinding.ActivityPrivacyBinding

class PrivacyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPrivacyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivacyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.turnbackBtn.setOnClickListener {
            finish()
        }
    }
}