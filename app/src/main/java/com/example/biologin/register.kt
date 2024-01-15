package com.example.biologin

import android.content.ContentValues
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore

class register : AppCompatActivity() {
    private lateinit var db:FirebaseFirestore
    private lateinit var firstn:TextInputEditText
    private lateinit var lastn:TextInputEditText
    private lateinit var nrcid:TextInputEditText
    private lateinit var posi:TextInputEditText
    private lateinit var fingerprintManager: FingerprintManagerCompat


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register2)

        getSupportActionBar()?.hide()
        db = FirebaseFirestore.getInstance()
        fingerprintManager = FingerprintManagerCompat.from(this)

        val recieve=intent
        val userid = recieve.getStringExtra("userid")
        val useremail = recieve.getStringExtra("email")
        firstn=findViewById(R.id.firstnameid)
        lastn=findViewById(R.id.lastnameid)
        nrcid=findViewById(R.id.nrcid)
        posi=findViewById(R.id.positionid)

        val currentuserid=userid.toString()
        val collectionReference = db.collection("users")
        val documentReference=collectionReference.document(currentuserid)


        findViewById<Button>(R.id.btncontinue).setOnClickListener{
            val firstname=firstn.text.toString().trim()
            val lastname=lastn.text.toString().trim()
            val nrc:String=nrcid.text.toString().trim()
            val position:String=posi.text.toString().trim()
            val userid:String=userid.toString().trim()
            val useremail:String=useremail.toString().trim()



            documentReference.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result

                    // If the document exists, do something
                    if (document != null && document.exists()) {
                        Toast.makeText(this,"User already exists",Toast.LENGTH_SHORT).show()
                    } else {

                        val data = hashMapOf(
                            "firstname" to firstname,
                            "lastname" to lastname,
                            "fingerid" to Build.FINGERPRINT,
                            "position" to position,
                            "nrc" to nrc,
                            "userid" to userid,
                            "useremail" to useremail
                        )

                        documentReference.set(data)
                            .addOnSuccessListener {
                                // Data added successfully
                                Toast.makeText(this,"Registration Succeded",Toast.LENGTH_SHORT).show()
                                val intent=Intent(this@register,MainActivity::class.java)
                                startActivity(intent)
                            }
                            .addOnFailureListener { e ->
                                // Error adding data
                                Log.w(ContentValues.TAG, "Error adding document", e)
                            }

                    }
                } else {
                    // Handle the error
                    println(task.exception)
                }
            }

            val intent=Intent(this, login::class.java)
            startActivity(intent)


        }

    }
}