package com.cuongphan.vanastudio_v2.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.cuongphan.vanastudio_v2.databinding.ActivityProfileBinding
import com.cuongphan.vanastudio_v2.databinding.DialogConfirmSignoutBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser != null) {
            binding.profileInformation.visibility = View.VISIBLE
            binding.noUser.visibility = View.GONE

            val uid = FirebaseAuth.getInstance().currentUser?.uid
            val cusAvatar = FirebaseDatabase.getInstance().getReference("Customers").child(uid!!).child("profile").child("avatar")
            cusAvatar.get().addOnCompleteListener { snapshot ->
                val avatarUrl = snapshot.result.getValue(String::class.java)
                Glide.with(this).load(avatarUrl).into(binding.profilePic)
            }
            val cusName = FirebaseDatabase.getInstance().getReference("Customers").child(uid!!).child("profile").child("name")
            cusName.get().addOnCompleteListener { snapshot ->
                val name = snapshot.result.getValue(String::class.java)
                binding.profileName.text = name
            }
            val cusPhone = FirebaseDatabase.getInstance().getReference("Customers").child(uid!!).child("profile").child("phone")
            cusPhone.get().addOnCompleteListener { snapshot ->
                val phone = snapshot.result.getValue(String::class.java)
                binding.profilePhone.text = phone
            }
            val cusEmail = FirebaseDatabase.getInstance().getReference("Customers").child(uid!!).child("profile").child("email")
            cusEmail.get().addOnCompleteListener { snapshot ->
                val email = snapshot.result.getValue(String::class.java)
                binding.profileEmail.text = email
            }
        }else{
            binding.profileInformation.visibility = View.GONE
            binding.noUser.visibility = View.VISIBLE
        }

        binding.profileSignin.setOnClickListener {
            val intent = Intent(this, SigninActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // go to edit profile:
        binding.editProfileBtn.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }

        // go to edit address:
        binding.addressBtn.setOnClickListener {
            startActivity(Intent(this, AddressActivity::class.java))
        }

        // setup for logout:
        binding.logOutBtn.setOnClickListener {
            showConfirmationDialog()
        }

        // go to order history:
        binding.orderHistoryBtn.setOnClickListener {
            startActivity(Intent(this, OrderActivity::class.java))
        }

        // go to privacy:
        binding.policyBtn.setOnClickListener {
            startActivity(Intent(this, PrivacyActivity::class.java))
        }

        // go to help center:
        binding.helpCenterBtn.setOnClickListener {
            startActivity(Intent(this, HelpCenterActivity::class.java))
        }

        // setup for bottom menu:
        binding.profileHomeBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        binding.profileCartBtn.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
    }

    private fun showConfirmationDialog() {
        val dialogBinding = DialogConfirmSignoutBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogBinding.root)
        dialog.setCancelable(false)
        dialogBinding.cancelBtn.setOnClickListener { dialog.dismiss() }
        dialogBinding.yesBtn.setOnClickListener {
            dialog.dismiss()
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
        dialog.show()
    }
}