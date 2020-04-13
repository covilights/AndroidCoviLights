package com.covilights

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.covilights.beacon.BeaconCallback
import com.covilights.beacon.BeaconManager
import com.covilights.beacon.BeaconState
import com.covilights.databinding.DebugActivityBinding
import com.covilights.user.UserManager
import org.koin.android.ext.android.inject

class DebugActivity : AppCompatActivity() {

    lateinit var binding: DebugActivityBinding

    private val userManager: UserManager by inject()
    private val beaconManager: BeaconManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DebugActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initUi()

        binding.user.text = "User: ${userManager.userUuid}"

        checkPermissions()

        beaconManager.state.observe(this, Observer { state ->
            log("Beacon state: $state")

            when (state) {
                BeaconState.STARTED -> {
                    binding.start.isVisible = false
                    binding.stop.isVisible = true
                }
                else -> {
                    binding.start.isVisible = true
                    binding.stop.isVisible = false
                }
            }
        })

        beaconManager.results.observe(this, Observer { beacons ->
            binding.result.text = beacons.values.joinToString("\n--------------------------------\n") {
                "User: ${it.userUuid}\n" +
                    "isVisible: ${it.isVisible}\n" +
                    "isNear: ${it.isNear}\n" +
                    "MinDistance: ${"%.2f".format(it.minDistanceInMeter)}m\n" +
                    "LastDistance: ${"%.2f".format(it.distanceInMeter)}m"
            }

            log("Result: $beacons")
        })

        if (!beaconManager.hasBleFeature()) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun initUi() {
        binding.log.movementMethod = ScrollingMovementMethod()
        binding.start.setOnClickListener(::onStartClick)
        binding.stop.setOnClickListener(::onStopClick)
    }

    private fun checkPermissions() {
        val permissions: Array<String> = arrayOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION
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
        permissions: Array<String>,
        grantResults: IntArray
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

    private fun onStartClick(v: View) {
        if (!beaconManager.isBluetoothEnabled()) {
            log("Enabling bluetooth...")

            beaconManager.enableBluetooth(object : BeaconCallback {
                override fun onSuccess() {
                    log("Bluetooth is on now")
                }

                override fun onError(throwable: Throwable) {
                    log(throwable.message ?: throwable.toString())
                }
            })
        } else {
            beaconManager.start(object : BeaconCallback {
                override fun onSuccess() {
                    log("Beacon started successfully!")
                }

                override fun onError(throwable: Throwable) {
                    log(throwable.message ?: throwable.toString())
                }
            })
        }
    }

    private fun onStopClick(v: View) {
        beaconManager.stop(object : BeaconCallback {
            override fun onSuccess() {
                log("Beacon stopped successfully!")
            }

            override fun onError(throwable: Throwable) {
                log(throwable.message ?: throwable.toString())
            }
        })
    }

    private fun log(message: String) {
        runOnUiThread {
            binding.log.text = "$message\n" +
                "-----------------------------------\n" +
                "${binding.log.text}"
        }
    }

    companion object {
        const val PERMISSIONS_REQUEST = 1010
        const val USER_UUID = "user_uuid"

        fun intent(context: Context) =
            Intent(context, DebugActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
    }
}
