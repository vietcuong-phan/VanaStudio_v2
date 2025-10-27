package com.cuongphan.vanastudio_v2.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.cuongphan.vanastudio_v2.adapter.FaqAdapter
import com.cuongphan.vanastudio_v2.data_class.FaqItemData
import com.cuongphan.vanastudio_v2.databinding.FragmentFaqBinding

class FaqFragment : Fragment() {
    private var _binding: FragmentFaqBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: FaqAdapter
    private lateinit var faqList: MutableList<FaqItemData>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFaqBinding.inflate(inflater, container, false)
        faqList = mutableListOf(
            FaqItemData("What is Vana Studio?", "Vana Studio is an app that helps you buy every thing ..."),
            FaqItemData("How to use Vana Studio?", "Open the app, create an account, then ..."),
            FaqItemData("How do I cancel an order?", "Go to Orders page, select Cancel option ..."),
            FaqItemData("Is Vana Studio free to use?", "Yes, it is free with optional premium features ..."),
            FaqItemData("How to add promo on Vana Studio?", "You can add promo codes at checkout ...")
        )
        adapter = FaqAdapter(faqList)
        binding.recyclerViewFaq.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewFaq.adapter = adapter
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}