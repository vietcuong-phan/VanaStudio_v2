package com.cuongphan.vanastudio_v2.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cuongphan.vanastudio_v2.R
import com.cuongphan.vanastudio_v2.data_class.Address
import com.cuongphan.vanastudio_v2.data_class.CustomerData
import com.cuongphan.vanastudio_v2.data_class.Profile
import com.cuongphan.vanastudio_v2.databinding.ActivitySignupBinding
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import com.cuongphan.vanastudio_v2.data_class.Metadata

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth
    private val database = FirebaseDatabase.getInstance().reference
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var callbackManager: CallbackManager
    private val now = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())

    companion object {
        private const val RC_SIGN_IN = 9001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        callbackManager = CallbackManager.Factory.create()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.signupBtn.setOnClickListener {
            val name = binding.nameEdt.text.toString().trim()
            val email = binding.emailEdt.text.toString().trim()
            val password = binding.passwordEdt.text.toString().trim()
            val phone = binding.phoneEdt.text.toString().trim()
            val addressInput = binding.addressEdt.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty() && phone.isNotEmpty() && addressInput.isNotEmpty()){

                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful){
                            val cusId = auth.currentUser?.uid
                            val user = auth.currentUser
                            val addressId = UUID.randomUUID().toString()
                            val address = Address(
                                title = "Home",
                                details = addressInput
                            )

                            val addressMap = mapOf(addressId to address)
                            val profile = Profile(
                                name = name,
                                email = email,
                                phone = phone,
                                address = addressMap,
                                gender = "",
                                birthday = "",
                                country = "",
                                avatar = "https://png.pngtree.com/png-clipart/20240321/original/pngtree-avatar-job-student-flat-portrait-of-man-png-image_14639683.png"
                            )
                            val metadata = Metadata(created_at = now, role = "customer", status = "active", provider = "email")
                            val customer = CustomerData(metadata, profile)
                            val profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(name).build()

                            if (cusId != null) {
                                user?.updateProfile(profileUpdates)
                                database.child("Customers").child(cusId).setValue(customer)
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "Create account successfully!", Toast.LENGTH_SHORT).show()
                                        val intent = Intent(this, SigninPasswordActivity::class.java)
                                        finish()
                                        startActivity(intent)
                                    }.addOnFailureListener {
                                        Toast.makeText(this, "Create account failed: ${it.message}", Toast.LENGTH_SHORT).show()
                                    }
                            }else{
                                Toast.makeText(this, "Create account failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
            }else{
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
        binding.signinBtn.setOnClickListener {
            val intent = Intent(this, SigninActivity::class.java)
            finish()
            startActivity(intent)
        }

        binding.backBtn.setOnClickListener { finish() }

        binding.wechatBtn.setOnClickListener { Toast.makeText(this, "Coming soon!", Toast.LENGTH_SHORT).show() }

        binding.googleBtn.setOnClickListener {
            signInWithGoogle()
        }
        binding.facebookBtn.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(this, listOf("email", "public_profile"))
            LoginManager.getInstance().registerCallback(callbackManager,
                object : FacebookCallback<LoginResult> {
                    override fun onCancel() {
                        Toast.makeText(this@SignupActivity, "Sign in canceled.", Toast.LENGTH_SHORT).show()
                    }

                    override fun onError(error: FacebookException) {
                        Toast.makeText(this@SignupActivity, "Sign in error: ${error.message}", Toast.LENGTH_SHORT).show()
                    }

                    override fun onSuccess(result: LoginResult) {
                        handleFacebookAccessToken(result.accessToken)
                    }

                })
        }
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val userMap = mapOf(
                        "name" to user?.displayName,
                        "email" to user?.email,
                        "avatar" to user?.photoUrl.toString(),
                        "phone" to user?.phoneNumber
                    )
                    val uid = user?.uid
                    val dbRef = FirebaseDatabase.getInstance().getReference("Customers").child(uid!!).child("profile")
                    dbRef.setValue(userMap)
                    val mdRef = FirebaseDatabase.getInstance().getReference("Customers").child(uid).child("metadata")
                    mdRef.setValue(Metadata(created_at = now, role = "customer", status = "active", provider = "facebook"))

                    Toast.makeText(this, "Hallo ${user.displayName},\nHave a nice day!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })
                } else {
                    Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                account?.idToken?.let { firebaseAuthWithGoogle(it) }
            } catch (e: ApiException) {
                Toast.makeText(this, "Google sign in failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val userMap = mapOf(
                        "name" to user?.displayName,
                        "email" to user?.email,
                        "avatar" to user?.photoUrl.toString(),
                        "phone" to user?.phoneNumber
                    )
                    val uid = user?.uid
                    val dbRef = FirebaseDatabase.getInstance().getReference("Customers").child(uid!!).child("profile")
                    Toast.makeText(this, "Hallo ${user.displayName},\nHave a nice day!", Toast.LENGTH_SHORT).show()
                    dbRef.setValue(userMap)
                    val mdRef = FirebaseDatabase.getInstance().getReference("Customers").child(uid).child("metadata")
                    mdRef.setValue(Metadata(created_at = now, role = "customer", status = "active", provider = "google"))

                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show()
                }
            }
    }
}