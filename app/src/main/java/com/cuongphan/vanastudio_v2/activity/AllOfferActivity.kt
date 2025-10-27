package com.cuongphan.vanastudio_v2.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cuongphan.vanastudio_v2.adapter.OfferAdapter
import com.cuongphan.vanastudio_v2.data_class.OfferData
import com.cuongphan.vanastudio_v2.databinding.ActivityAllOfferBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AllOfferActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAllOfferBinding
    private lateinit var adapter: OfferAdapter
    private val offerList = mutableListOf<OfferData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllOfferBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtn.setOnClickListener { finish() }

        binding.listOfferPrb.visibility = View.GONE
        adapter = OfferAdapter(offerList)
        binding.listOfferRcv.adapter = adapter

        val database = FirebaseDatabase.getInstance().getReference("Offers")
        database.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                offerList.clear()
                for (data in snapshot.children) {
                    val offer = data.getValue(OfferData::class.java)
                    if (offer != null) {
                        offerList.add(offer)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AllOfferActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}