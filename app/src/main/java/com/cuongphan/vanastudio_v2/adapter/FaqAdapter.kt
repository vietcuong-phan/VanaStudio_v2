package com.cuongphan.vanastudio_v2.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cuongphan.vanastudio_v2.data_class.FaqItemData
import com.cuongphan.vanastudio_v2.databinding.ItemFaqBinding

class FaqAdapter(private val faqList: MutableList<FaqItemData>) :
    RecyclerView.Adapter<FaqAdapter.FaqViewHolder>() {

    inner class FaqViewHolder(val binding: ItemFaqBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FaqViewHolder {
        val binding = ItemFaqBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FaqViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FaqViewHolder, position: Int) {
        val faq = faqList[position]
        holder.binding.tvQuestion.text = faq.question
        holder.binding.tvAnswer.text = faq.answer
        holder.binding.tvAnswer.visibility = if (faq.isExpanded) View.VISIBLE else View.GONE
        holder.binding.view15.visibility = if (faq.isExpanded) View.VISIBLE else View.GONE
        holder.binding.root.setOnClickListener {
            faq.isExpanded = !faq.isExpanded
            notifyItemChanged(position)
        }
    }

    override fun getItemCount() = faqList.size
}