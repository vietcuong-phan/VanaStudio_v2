package com.cuongphan.vanastudio_v2.activity

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cuongphan.vanastudio_v2.databinding.ActivityEditProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar
import java.util.Locale

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        setupCountryDropdown()
        setupGenderDropdown()
        loadProfile()

        binding.updateBtn.setOnClickListener {
            updateProfileToFirebase()
        }
        binding.gobackBtn.setOnClickListener { finish() }
    }

    private fun setupGenderDropdown() {
        val genders = listOf("Male", "Female")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, genders)
        binding.genderDropdown.setAdapter(adapter)
    }

    private fun setupCountryDropdown() {
        val locales = Locale.getISOCountries()
        val countryNames = mutableListOf<String>()

        for (code in locales) {
            val locale = Locale("", code)
            val countryName = locale.getDisplayCountry(Locale.ENGLISH)
            if (countryName.isNotBlank()) {
                countryNames.add(countryName)
            }
        }
        val sortedCountries = countryNames.sorted()
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            sortedCountries
        )
        binding.countryDropdown.setAdapter(adapter)
        binding.countryDropdown.setOnClickListener {
            binding.countryDropdown.showDropDown()
        }
        binding.countryDropdown.setOnItemClickListener { parent, view, position, id ->
            val selectedCountry = sortedCountries[position]
            println("Selected country: $selectedCountry")
        }
        binding.birthdayEdt.setOnClickListener {
            showDatePickerDialog()
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker = android.app.DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                // Format thành dd/MM/yyyy
                val formatted = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
                binding.birthdayEdt.setText(formatted)
            },
            year,
            month,
            day
        )
        datePicker.datePicker.maxDate = System.currentTimeMillis()
        datePicker.show()
    }

    private fun updateProfileToFirebase() {
        val fullName = binding.fullNameEdt.text.toString().trim()
        val birthday = binding.birthdayEdt.text.toString().trim()
        val email = binding.pro5emailEdt.text.toString().trim()
        val country = binding.countryDropdown.text.toString().trim()
        val phone = binding.pro5phoneEdt.text.toString().trim()
        val gender = binding.genderDropdown.text.toString().trim()

        if (fullName.isEmpty() || email.isEmpty() || country.isEmpty() || phone.isEmpty() || gender.isEmpty() || birthday.isEmpty()) {
            Toast.makeText(this, "Please fill in required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val uid = auth.currentUser?.uid ?: return
        val updates = mapOf(
            "name" to fullName,
            "birthday" to birthday,
            "email" to email,
            "country" to country,
            "phone" to phone,
            "gender" to gender
        )
        val userRef = database.getReference("Customers").child(uid).child("profile")
        userRef.updateChildren(updates)
            .addOnSuccessListener {
                Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Update failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadProfile() {
        val uid = auth.currentUser?.uid ?: return
        val userRef = database.getReference("Customers").child(uid).child("profile")

        userRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val fullName = snapshot.child("name").value?.toString()
                val birthday = snapshot.child("birthday").value?.toString()
                val email = snapshot.child("email").value?.toString()
                val country = snapshot.child("country").value?.toString()
                val phone = snapshot.child("phone").value?.toString()
                val gender = snapshot.child("gender").value?.toString()

                if (!fullName.isNullOrBlank()) binding.fullNameEdt.setText(fullName)
                if (!birthday.isNullOrBlank()) binding.birthdayEdt.setText(birthday)
                if (!email.isNullOrBlank()) binding.pro5emailEdt.setText(email)
                if (!country.isNullOrBlank()) binding.countryDropdown.setText(country, false)
                if (!phone.isNullOrBlank()) binding.pro5phoneEdt.setText(phone)
                if (!gender.isNullOrBlank()) binding.genderDropdown.setText(gender, false)
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to load profile: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }
}