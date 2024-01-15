package com.example.biologin

import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.Manifest
import android.provider.Settings
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private val READ_EXTERNAL_STORAGE_REQUEST_CODE = 100


    private lateinit var db: FirebaseFirestore
    private lateinit var user:TextView
    private lateinit var firebaseauth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = FirebaseFirestore.getInstance()
        firebaseauth = FirebaseAuth.getInstance()



        val currentUser = firebaseauth.currentUser
        if (currentUser != null) {
            val userName = currentUser.email
            val nameTextView = findViewById<TextView>(R.id.usernameid)
            nameTextView.setText(userName)
        }

        val logtimecollection = db.collection("logdata")
        val documentReference = logtimecollection.document(firebaseauth.currentUser?.uid.toString())
        val attendancerecords = "records"

        val sublogdata = db.collection("logdata")
            .document(firebaseauth.currentUser?.uid.toString())
            .collection(attendancerecords)


        val query = db.collection("logdata")
            .document(firebaseauth.currentUser?.uid.toString())
            .collection(attendancerecords)
            .whereEqualTo("id", firebaseauth.currentUser?.uid)




        query.get().addOnSuccessListener { querySnapshot ->
            val dataList: kotlin.collections.ArrayList<logdata> =
                querySnapshot.documents.map { documentSnapshot ->
                    documentSnapshot.toObject(logdata::class.java)
                } as ArrayList<logdata>


            val Dataadapter = logAdapter(dataList)
            val recyclerView = findViewById<RecyclerView>(R.id.recyclerid)
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = Dataadapter


            val currentTime = Date()
            val currentdate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val currenttimeo = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val formattedDate = currentdate.format(currentTime)
            val formattedTime = currenttimeo.format(currentTime)

            var time = LocalTime.now()
            var date = Date().toString()
            var logintime = formattedTime
            var logouttime = formattedTime
            var day = formattedDate



            val query = sublogdata.whereEqualTo("id", firebaseauth.currentUser?.uid)
                .whereEqualTo("date", day)
            val logoutbutton: Button = findViewById(R.id.logouttime)
            logoutbutton.setOnClickListener {
                // Get the document snapshot.
                query.get().addOnSuccessListener { querySnapshot ->
                    val documentSnapshot = querySnapshot.documents[0]
                    documentSnapshot.reference.update(mapOf("timeout" to formattedTime))
                    Toast.makeText(this, "Logged out succesfully", Toast.LENGTH_SHORT).show()
                }
                query.get().addOnFailureListener { exception ->

                    Toast.makeText(
                        this,
                        "Failed to log out due to: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()

                }


            }




            val subquery = db.collection("logdata")
                .document(firebaseauth.currentUser?.uid.toString())
                .collection(attendancerecords)
                .whereEqualTo("date", day)

            val loginbutton = findViewById<Button>(R.id.logtime)

            loginbutton.setOnClickListener {
                documentReference.get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        subquery.get()
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val querySnapshot: QuerySnapshot? = task.result
                                    if (querySnapshot != null && !querySnapshot.isEmpty) {
                                        Toast.makeText(this, "You already logged in:", Toast.LENGTH_SHORT).show()
                                    } else {
                                        // The value is unique, you can proceed to add it to Firestore
                                        // Add your code here to add the value to Firestore
                                        val logindata = hashMapOf(
                                            "timein" to logintime,
                                            "date" to day,
                                            "timeout" to "",
                                            "id" to firebaseauth.currentUser?.uid
                                        )
                                        documentReference
                                            .collection(attendancerecords)
                                            .add(logindata)
                                            .addOnSuccessListener {
                                                // Data added successfully
                                                Toast.makeText(
                                                    this,
                                                    "You have logged in",
                                                    Toast.LENGTH_SHORT
                                                )
                                                    .show()

                                            }
                                            .addOnFailureListener { e ->
                                                // Error adding data
                                                Log.w(ContentValues.TAG, "Error adding document", e)
                                            }
                                    }
                                } else {

                                    Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
                                }
                            }

                    }
                }
            }
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val user=firebaseauth.currentUser
        when (item.itemId) {
            R.id.logutid -> {
                if(user !=null){
                    firebaseauth.signOut()
                    val intent=Intent(this,login::class.java)
                    startActivity(intent)
                }
                return true
            }
            R.id.settingsid -> {
                // Handle item 2 click
                return true
            }

            R.id.send -> {
                // Handle item 2 click


                val db = FirebaseFirestore.getInstance()
                val attendancerecords = "records"

                val query = db.collection("logdata")
                    .document(firebaseauth.currentUser?.uid.toString())
                    .collection(attendancerecords)
                    .whereEqualTo("id", firebaseauth.currentUser?.uid)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        // Process the Firestore data and create an Excel (CSV) file
                        val csvFileName = "Attendance data.csv"
                        val csvFile = File(filesDir, csvFileName)
                        val csvWriter = FileWriter(csvFile)

                        querySnapshot.forEach { documentSnapshot ->
                            val data = documentSnapshot.data
                            val row = data.values.joinToString(",")
                            csvWriter.appendln(row)
                        }

                        csvWriter.close()


                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            // Create an Intent object with the ACTION_SEND action
                            sendEmailWithAttachment(csvFile, csvFileName)
                        } else {
                            // If the permission is not granted, request it again
                            ActivityCompat.requestPermissions(
                                this,
                                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                                READ_EXTERNAL_STORAGE_REQUEST_CODE
                            )

                        }


                        // Send the Excel file as an email attachment

                    }
                    .addOnFailureListener { exception ->
                       Toast.makeText(this,"Failed to get data from database",Toast.LENGTH_SHORT).show()
                    }

            return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }


//funtion to send the login data via email as an excel file
    private fun sendEmailWithAttachment(file: File, fileName: String) {
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.type = "text/csv"
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("mwanzapatel@gmail.com"))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Attendance Data")
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Here is your reporting data")
        val uri = Uri.fromFile(file)
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri)
        startActivity(Intent.createChooser(emailIntent, "Send email..."))
    }

    }

