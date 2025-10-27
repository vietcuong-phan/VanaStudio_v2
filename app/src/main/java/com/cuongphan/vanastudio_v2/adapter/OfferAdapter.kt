package com.cuongphan.vanastudio_v2.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cuongphan.vanastudio_v2.data_class.OfferData
import com.cuongphan.vanastudio_v2.databinding.ItemOfferBinding

class OfferAdapter(
    private val offerList: List<OfferData>
): RecyclerView.Adapter<OfferAdapter.OfferViewHolder>() {

    inner class OfferViewHolder(val binding: ItemOfferBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfferViewHolder {
        val binding = ItemOfferBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OfferViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OfferViewHolder, position: Int) {
        val offer = offerList[position]
        with(holder.binding) {
            discountOffer.text = offer.discount
            titleOffer.text = offer.title
            Glide.with(root.context)
                .load(offer.img_url)
                .into(offerPic)
        }
    }

    override fun getItemCount(): Int = offerList.size
}