package com.covilights

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.covilights.databinding.MainActivityBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    lateinit var binding: MainActivityBinding

    private val dateFormat = SimpleDateFormat("hh:mm:ss", Locale.US)

    private val advertiseManager = AdvertiseManager(object : AdvertiseManager.OnAdvertiseListener {
        override fun onSuccess() {
            log("Advertising started successfully!")
        }

        override fun onError(throwable: Throwable) {
            log(throwable.message ?: throwable.toString())
        }
    })

    private val scanManager = ScanManager(object : ScanManager.OnScanListener {
        override fun onScanResult(result: String) {
            log(result)
        }

        override fun onError(throwable: Throwable) {
            log(throwable.message ?: throwable.toString())
        }
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initUi()

        checkPermissions()
    }

    private fun initUi() {
        binding.log.movementMethod = ScrollingMovementMethod()
        binding.advertise.setOnClickListener(::onAdvertiseClick)
        binding.discover.setOnClickListener(::onDiscoverClick)
    }

    private fun checkPermissions() {
        val permissions = arrayOf<String>(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        permissions.forEach { permission ->
            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                // Permission is not granted
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        permission
                    )
                ) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(
                        this,
                        permissions,
                        PERMISSIONS_REQUEST
                    )

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            } else {
                // Permission has already been granted
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    private fun onAdvertiseClick(v: View) {
        advertiseManager.startAdvertising()
    }

    private fun onDiscoverClick(v: View) {
        scanManager.startScan()
    }

    private fun log(message: String) {
        val date = Date()
        runOnUiThread {
            binding.log.text = "${dateFormat.format(date)} - " +
                "$message\n" +
                "-----------------------------------\n" +
                "${binding.log.text}"
        }
    }

    companion object {
        const val PERMISSIONS_REQUEST = 1010
        const val USER_UUID = "user_uuid"
    }
}
