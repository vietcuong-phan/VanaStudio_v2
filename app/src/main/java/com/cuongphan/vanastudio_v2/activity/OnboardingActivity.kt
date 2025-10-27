package com.cuongphan.vanastudio_v2.activity

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.cuongphan.vanastudio_v2.R
import com.cuongphan.vanastudio_v2.adapter.OnboardingAdapter
import com.cuongphan.vanastudio_v2.data_class.OnboardingData
import com.cuongphan.vanastudio_v2.databinding.ActivityOnboardingBinding
import com.google.firebase.auth.FirebaseAuth

class OnboardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val items = listOf(
            OnboardingData(
                R.drawable.image1,
                "We provide high quality products just for you",
                "Next"
            ),
            OnboardingData(R.drawable.image2, "Your satisfaction is our number one priority", "Next"),
            OnboardingData(R.drawable.image3, "Let's fulfill your daily needs with Vana right now!", "Get Started")
        )

        val adapter = OnboardingAdapter(items) { position ->
            if (position < items.size - 1) {
                binding.viewpager.currentItem = position + 1
            } else {
                startActivity(Intent(this, SigninActivity::class.java))
                finish()
            }
        }

        binding.viewpager.adapter = adapter
        binding.dotsIndicator.attachTo(binding.viewpager)
    }

    override fun onStart() {
        super.onStart()
        val auth = FirebaseAuth.getInstance().currentUser
        if (auth != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}