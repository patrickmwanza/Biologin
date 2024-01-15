package com.example.biologin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.biologin.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class signup : AppCompatActivity() {
    private lateinit var binding:ActivitySignupBinding
    private lateinit var  firebaseauthentication:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getSupportActionBar()?.hide()
        binding=ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseauthentication=FirebaseAuth.getInstance()

        binding.signUpButton.setOnClickListener{
            val email=binding.emailEditText.text.toString()
            val password=binding.passwordEditText.text.toString()
            val comfirmpassword=binding.confirmPasswordEditText.text.toString()

            if(email.isNotBlank() && password.isNotEmpty() && comfirmpassword.isNotEmpty()){
                if (password==comfirmpassword){

                    firebaseauthentication.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {

                                val user: FirebaseUser? = firebaseauthentication.currentUser
                                if (user != null) {
                                    val uid = user.uid
                                    val intent= Intent(this,register::class.java)
                                    intent.putExtra("userid",uid)
                                    intent.putExtra("email",email)
                                    startActivity(intent)
                                }


                            } else {
                                Toast.makeText(this,"Signup failed: ${task.exception?.message}",Toast.LENGTH_SHORT).show()
                                Log.e("SignUpActivity", "Sign-up failed: ${task.exception?.message}")
                            }
                        }
                }else{
                    Toast.makeText(this,"Password not matching",Toast.LENGTH_SHORT).show()
                }

            }
            else{
                Toast.makeText(this,"All fields are required",Toast.LENGTH_SHORT).show()
            }
        }

    }
}