package com.cuongphan.vanastudio_v2.activity

import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cuongphan.vanastudio_v2.R
import com.cuongphan.vanastudio_v2.adapter.AddressAdapter
import com.cuongphan.vanastudio_v2.data_class.Address
import com.cuongphan.vanastudio_v2.databinding.ActivityAddressBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AddressActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddressBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var adapter: AddressAdapter
    private val addressMap = mutableMapOf<String, Address>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid ?: return

        binding.backkBtn.setOnClickListener { finish() }
        binding.addNewBtn.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    AddAddressActivity::class.java
                )
            )
        }

        adapter = AddressAdapter(emptyList()) { address ->
            val addressId = addressMap.entries.find { it.value == address }?.key
            if (addressId != null) {
                val intent = Intent(this, EditAddressActivity::class.java)
                intent.putExtra("addressId", addressId)
                startActivity(intent)
            }

        }
        binding.addressListRcv.apply {
            layoutManager = LinearLayoutManager(this@AddressActivity)
            adapter = this@AddressActivity.adapter
        }
        database = FirebaseDatabase.getInstance()
            .getReference("Customers").child(uid).child("profile").child("address")
        loadAddress()

        val itemTouchHelper = ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

//            override fun onMove(
//                recyclerView: RecyclerView,
//                viewHolder: RecyclerView.ViewHolder,
//                target: RecyclerView.ViewHolder
//            ): Boolean = false
//
//            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//                val position = viewHolder.adapterPosition
//                val address = adapter.currentList[position]
//                val addressId = addressMap.entries.find { it.value == address }?.key
//
//                if (addressId != null) {
//                    val ref = FirebaseDatabase.getInstance()
//                        .getReference("Customers").child(uid)
//                        .child("profile").child("address").child(addressId)
//
//                    ref.removeValue().addOnCompleteListener {
//                        if (it.isSuccessful) {
//                            adapter.removeItem(position)
//                        }
//                    }
//                }
//            }
//
//            // ✅ Vẽ nền đỏ + icon thùng rác khi vuốt
//            override fun onChildDraw(
//                c: Canvas,
//                recyclerView: RecyclerView,
//                viewHolder: RecyclerView.ViewHolder,
//                dX: Float,
//                dY: Float,
//                actionState: Int,
//                isCurrentlyActive: Boolean
//            ) {
//                val itemView = viewHolder.itemView
//                val paint = Paint()
//
//                if (dX < 0) { // Vuốt sang trái
//                    paint.color = Color.RED
//                    c.drawRect(
//                        itemView.right.toFloat() + dX, itemView.top.toFloat(),
//                        itemView.right.toFloat(), itemView.bottom.toFloat(), paint
//                    )
//
//                    val icon = ContextCompat.getDrawable(recyclerView.context, R.drawable.ic_delete)
//                    icon?.let {
//                        val iconMargin = (itemView.height - it.intrinsicHeight) / 2
//                        val iconTop = itemView.top + iconMargin
//                        val iconLeft = itemView.right - iconMargin - it.intrinsicWidth
//                        val iconRight = itemView.right - iconMargin
//                        val iconBottom = iconTop + it.intrinsicHeight
//                        it.setBounds(iconLeft, iconTop, iconRight, iconBottom)
//                        it.draw(c)
//                    }
//                }
//
//                super.onChildDraw(
//                    c,
//                    recyclerView,
//                    viewHolder,
//                    dX,
//                    dY,
//                    actionState,
//                    isCurrentlyActive
//                )
//            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(
                viewHolder: RecyclerView.ViewHolder,
                direction: Int
            ) {
                val position = viewHolder.adapterPosition
                val address = adapter.currentList[position]
                val addressId = addressMap.entries.find { it.value == address }?.key

                if (addressId != null) {
                    val ref = FirebaseDatabase.getInstance()
                        .getReference("Customers").child(uid)
                        .child("profile").child("address").child(addressId)

                    ref.removeValue().addOnCompleteListener {
                        if (it.isSuccessful) {
                            adapter.removeItem(position)
                        }
                    }
                }
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
                val paint = Paint()
                if (dX < 0) {
                    paint.color = Color.RED
                    c.drawRect(
                        itemView.right.toFloat() + dX, itemView.top.toFloat(),
                        itemView.right.toFloat(), itemView.bottom.toFloat(), paint
                    )

                    val icon = ContextCompat.getDrawable(recyclerView.context, R.drawable.bin)
                    icon?.let {
                        val iconSize = 70
                        val iconMargin = (itemView.height - iconSize) / 2
                        val iconTop = itemView.top + iconMargin
                        val iconLeft = itemView.right - iconMargin - iconSize
                        val iconRight = itemView.right - iconMargin
                        val iconBottom = iconTop + iconSize
                        it.setBounds(iconLeft, iconTop, iconRight, iconBottom)
//                        it.draw(c)
                        (it as? android.graphics.drawable.BitmapDrawable)?.apply {
                            val paint = Paint()
                            paint.colorFilter = android.graphics.PorterDuffColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.SRC_IN)
                            c.save()
                            c.drawBitmap(bitmap, null, it.bounds, paint)
                            c.restore()
                        }

                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        })
        itemTouchHelper.attachToRecyclerView(binding.addressListRcv)
    }

    private fun loadAddress() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val addressList = mutableListOf<Address>()
                addressMap.clear()
                for (child in snapshot.children) {
                    val address = child.getValue(Address::class.java)
                    if (address != null) {
                        addressList.add(address)
                        addressMap[child.key ?: ""] = address
                    }
                }
                adapter.updateList(addressList)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}