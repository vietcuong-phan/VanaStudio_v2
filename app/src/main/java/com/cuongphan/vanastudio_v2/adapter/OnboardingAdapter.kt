package com.cuongphan.vanastudio_v2.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cuongphan.vanastudio_v2.data_class.OnboardingData
import com.cuongphan.vanastudio_v2.databinding.ItemOnboardingBinding

class OnboardingAdapter(
    private val items: List<OnboardingData>,
    private val onButtonClick: (position: Int) -> Unit
) : RecyclerView.Adapter<OnboardingAdapter.ViewHolder>(){

    inner class ViewHolder(val binding: ItemOnboardingBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemOnboardingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        with(holder.binding) {
            image.setImageResource(item.imageRes)
            title.text = item.title
            nextBtn.text = item.buttonText
            nextBtn.setOnClickListener { onButtonClick(position) }
        }
    }

    override fun getItemCount() = items.size
}