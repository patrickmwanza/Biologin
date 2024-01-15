package com.example.biologin

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import androidx.core.os.CancellationSignal
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import android.Manifest



class finger : AppCompatActivity() {


    private lateinit var fingerprintManager: FingerprintManagerCompat
    private lateinit var cancellationSignal: CancellationSignal

    private lateinit var resulttxt:TextView
    private lateinit var fingerprintid:TextView
    private lateinit var networkid:TextView
    private lateinit var auth: FirebaseAuth

    private lateinit var database: FirebaseDatabase
    private lateinit var usersRef: DatabaseReference
    private lateinit var db:FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finger)
        getSupportActionBar()?.hide()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE)
            != PackageManager.PERMISSION_GRANTED) {

            // Request the INTERNET permission.
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_NETWORK_STATE),
                NETWORK_PERMISSION_CODE)
        }else{
            Toast.makeText(this, "Permission already granted",Toast.LENGTH_SHORT).show()
        }
        resulttxt=findViewById(R.id.resulttxt)
        fingerprintid=findViewById(R.id.fingerprintid)
        fingerprintManager = FingerprintManagerCompat.from(this)
        cancellationSignal = CancellationSignal()
        auth=FirebaseAuth.getInstance()


        checkFingerprintAvailability()
        networkid=findViewById(R.id.network)
        var wifi=getUniqueConnectedWifiId()

        if(wifi==null){
            networkid.setText("Not connected to company network")

        }else{
            networkid.setText("Connected to company network${wifi}")

            checkFingerprintAvailability()
        }









    }





    companion object {
        private const val NETWORK_PERMISSION_CODE = 123
    }




    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            NETWORK_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission gra",Toast.LENGTH_SHORT).show()
                } else {

                    Toast.makeText(this, "Permission gra",Toast.LENGTH_SHORT).show()
                    // The user denied the INTERNET permission.
                    // Disable the network feature or notify the user that they need to grant
                    // the permission in order to use the feature.
                }
            }
        }}




    private fun checkFingerprintAvailability() {
        if (fingerprintManager.isHardwareDetected) {
            // Fingerprint hardware is available on the device
            if (fingerprintManager.hasEnrolledFingerprints()) {
                // Fingerprint is enrolled and can be used for authentication
                authenticateWithFingerprint()
            } else {
                // Fingerprint is not enrolled, prompt user to enroll
                promptUserToFingerprintEnrollment()
            }
        } else {
            // Fingerprint hardware is not available on the device
            resulttxt.text= "Fingerprint not available on this device."
        }
    }

    private fun authenticateWithFingerprint() {
        fingerprintManager.authenticate(
            null,
            0,
            cancellationSignal,
            object : FingerprintManagerCompat.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: FingerprintManagerCompat.AuthenticationResult?) {
                    // Fingerprint authentication succeeded

                    resulttxt.text= "Fingerprint authentication succeeded."
                    fingerprintid.visibility= View.VISIBLE

                    auth = FirebaseAuth.getInstance()

                    val currentUser = auth.currentUser

                    if (currentUser != null) {
                        val intent= Intent (applicationContext,MainActivity::class.java)
                        startActivity(intent)

                    } else {

                        val intent= Intent (applicationContext,login::class.java)
                        startActivity(intent)

                    }



                }

                override fun onAuthenticationFailed() {
                    // Fingerprint authentication failed
                    resulttxt.text = "Fingerprint authentication failed."
                    fingerprintid.text="No fingerprint found"
                }
            },
            null
        )
    }

    private fun promptUserToFingerprintEnrollment() {
        resulttxt.text= "No fingerprint enrolled. Please enroll a fingerprint in your device settings."
        val fingerscan=findViewById<ImageView>(R.id.fingerid).setOnClickListener {
            val intent = Intent(Settings.ACTION_SECURITY_SETTINGS)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onPause() {
        super.onPause()
        cancellationSignal.cancel()
    }


    fun getUniqueConnectedWifiId(): String? {
        val wifiManager = getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo
        val ssid = wifiInfo.ssid
        val mac=wifiInfo.macAddress
        val bssid = wifiInfo.bssid
        val networkid=mac+bssid
        return if (ssid != null && bssid != null) {
           // "$ssid:$bssid"
            networkid
        } else {
            null
        }
    }


}

