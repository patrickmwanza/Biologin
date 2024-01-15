package com.example.biologin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.biologin.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class login : AppCompatActivity() {
    private lateinit var  firebaseauth: FirebaseAuth
    private lateinit var binding:ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getSupportActionBar()?.hide()
        firebaseauth= FirebaseAuth.getInstance()

binding.gotosignup.setOnClickListener{
    val intent= Intent(this, signup::class.java)
    startActivity(intent)
}
        binding.signinButton.setOnClickListener{
            val user=binding.emailEditText.text.toString()
            val pass=binding.passwordEditText.text.toString()

            if(user.isNotEmpty() && pass.isNotEmpty()){
                firebaseauth.signInWithEmailAndPassword(user,pass)
                    .addOnCompleteListener(this){ task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this,"Login succeded",Toast.LENGTH_SHORT).show()

                            val user = firebaseauth.currentUser
                            val intent= Intent(this, MainActivity::class.java)
                            startActivity(intent)

                        } else {
                            // Login failed, display a message to the user
                            Toast.makeText(this,"Login failed: ${task.exception?.message}",Toast.LENGTH_SHORT).show()
                            Log.e("LoginActivity", "Login failed: ${task.exception?.message}")
                            // You can display an error message to the user
                        }
                    }

            }else{
                Toast.makeText(this,"All fields are required",Toast.LENGTH_SHORT).show()

            }


    }
}}