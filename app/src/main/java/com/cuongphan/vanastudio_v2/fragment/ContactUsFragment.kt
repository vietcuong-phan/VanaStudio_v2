package com.cuongphan.vanastudio_v2.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.cuongphan.vanastudio_v2.R
import com.cuongphan.vanastudio_v2.adapter.ContactAdapter
import com.cuongphan.vanastudio_v2.data_class.ActionType
import com.cuongphan.vanastudio_v2.data_class.ContactItemData
import com.cuongphan.vanastudio_v2.databinding.FragmentContactUsBinding

class ContactUsFragment : Fragment() {
    private var _binding: FragmentContactUsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactUsBinding.inflate(inflater, container, false)

        val contacts = listOf(
            ContactItemData(R.drawable.ic_headphones, "Customer Service", "", ActionType.CHAT),
            ContactItemData(R.drawable.phone_icon, "Hotline", "tel:0398508235", ActionType.CALL)
        )

        val adapter = ContactAdapter(requireContext(), contacts)
        binding.recyclerViewContact.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewContact.adapter = adapter

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}