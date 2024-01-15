package com.example.biologin

import LocationHelper
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import java.text.DecimalFormat
import kotlin.math.abs

class location : AppCompatActivity() {

    private val locationHelper by lazy { LocationHelper(this) }

    private val requiredLatitude = -15.4275499// Your required latitude
    private val requiredLongitude = 28.2966946 // Your required longitude

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val lastLocation = locationResult.lastLocation
            val userLatitude = lastLocation?.latitude
            val userLongitude = lastLocation?.longitude
val decimal=7
            val lattolerance= userLatitude!!.minus(requiredLatitude).let { abs(it) }
            val formattedlattolerance=formatDecimal(lattolerance,decimal)
            val longtolerance=userLongitude!!.minus(requiredLongitude).let{abs(it)}
            val formattedlongtolerance=formatDecimal(longtolerance,decimal)
            val lt=findViewById<TextView>(R.id.lattol)
            lt.setText(formattedlattolerance.toString())
            val lont=findViewById<TextView>(R.id.longtol)
            lont.setText(formattedlongtolerance.toString())

            val currlat:TextView=findViewById(R.id.currentlat)
            currlat.setText(userLatitude.toString())

            val currlong:TextView=findViewById(R.id.currentlog)
            currlong.setText(userLongitude.toString())

            if (isInRequiredLocation(userLatitude, userLongitude)) {
                // The device is in the required location, load your activity
                loadYourActivity()
            } else {

                message("You have not entered the premices of licef school")
                // Device is not in the required location
                // Handle the case when it's not in the required location
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_locaion)
        getSupportActionBar()?.hide()
        checkLocationPermission()

    }

    override fun onStart() {
        super.onStart()
        locationHelper.requestLocationUpdates(locationCallback)
        val reqlat=findViewById<TextView>(R.id.reqlat)
        reqlat.setText(requiredLatitude.toString())
        val reqlog: TextView =findViewById(R.id.reqlong)
        reqlog.setText(requiredLongitude.toString())


    }

    override fun onStop() {
        super.onStop()
        locationHelper.removeLocationUpdates(locationCallback)
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    private fun isInRequiredLocation(latitude: Double?, longitude: Double?): Boolean {
        // Check if the user's location is within a certain range of the required location
        val tolerance = 0.000300 // Adjust this value as needed
        return (Math.abs(latitude!! - requiredLatitude) < tolerance &&
                Math.abs(longitude!! - requiredLongitude) < tolerance)
    }

    private fun loadYourActivity() {
        // Load your activity here
        Toast.makeText(this,"Now in location", Toast.LENGTH_SHORT).show()
        val intent= Intent(this, finger::class.java)
        startActivity(intent)
    }

    // Handle permission request result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_LOCATION_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, start location updates
                    locationHelper.requestLocationUpdates(locationCallback)
                } else {
                    // Permission denied, handle accordingly
                    Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    fun formatDecimal(decimalValue: Double, decimalPlaces: Int): String {
        val decimalFormat = DecimalFormat("#." + "0".repeat(decimalPlaces))
        return decimalFormat.format(decimalValue)
    }


    fun message(message:String){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 123
    }

}