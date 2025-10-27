package com.cuongphan.vanastudio_v2.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.cuongphan.vanastudio_v2.fragment.ContactUsFragment
import com.cuongphan.vanastudio_v2.fragment.FaqFragment

class HelpCenterPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FaqFragment()
            1 -> ContactUsFragment()
            else -> FaqFragment()
        }
    }
}
