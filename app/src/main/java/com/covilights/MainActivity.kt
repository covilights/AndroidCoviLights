package com.covilights

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.covilights.beacon.BluetoothStateManager
import com.covilights.beacon.advertiser.BeaconAdvertiser
import com.covilights.beacon.advertiser.BeaconAdvertiserCallback
import com.covilights.beacon.advertiser.BeaconAdvertiserImpl
import com.covilights.beacon.advertiser.BeaconAdvertiserState
import com.covilights.beacon.scanner.BeaconScanner
import com.covilights.beacon.scanner.BeaconScannerCallback
import com.covilights.beacon.scanner.BeaconScannerImpl
import com.covilights.beacon.scanner.BeaconScannerState
import com.covilights.databinding.MainActivityBinding
import com.mirhoseini.appsettings.AppSettings
import java.util.Date
import java.util.UUID

class MainActivity : AppCompatActivity() {

    lateinit var binding: MainActivityBinding

    // private val dateFormat = SimpleDateFormat("hh:mm:ss", Locale.US)

    private val userUuid: String by lazy {
        AppSettings.getString(this, USER_UUID) ?: run {
            val newUuid = UUID.randomUUID().toString()
            AppSettings.setValue(this, USER_UUID, newUuid)
            return@run newUuid
        }
    }

    lateinit var bluetoothManager: BluetoothStateManager

    private val beaconAdvertiser: BeaconAdvertiser = BeaconAdvertiserImpl()
    private val beaconScanner: BeaconScanner = BeaconScannerImpl()

    private val scannerCallback = object : BeaconScannerCallback {
        override fun onSuccess() {
            log("Scanning started successfully!")
        }

        override fun onError(throwable: Throwable) {
            log(throwable.message ?: throwable.toString())
        }
    }

    private val advertiserCallback = object : BeaconAdvertiserCallback {
        override fun onSuccess() {
            log("Advertising started successfully!")
        }

        override fun onError(throwable: Throwable) {
            log(throwable.message ?: throwable.toString())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initUi()

        binding.user.text = "User: $userUuid"

        checkPermissions()

        bluetoothManager = BluetoothStateManager(this)

        beaconAdvertiser.state.observe(this, Observer { state ->
            state ?: return@Observer

            when (state) {
                BeaconAdvertiserState.IDLE -> log("Advertiser state: idle")
                BeaconAdvertiserState.RUNNING -> log("Advertiser state: running")
            }
        })

        beaconScanner.state.observe(this, Observer { state ->
            state ?: return@Observer

            when (state) {
                BeaconScannerState.IDLE -> log("Scanner state: idle")
                BeaconScannerState.RUNNING -> log("Scanner state: running")
            }
        })
    }

    private fun initUi() {
        binding.log.movementMethod = ScrollingMovementMethod()
        binding.advertise.setOnClickListener(::onAdvertiseClick)
        binding.discover.setOnClickListener(::onDiscoverClick)
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
        if (!bluetoothManager.getBluetoothStatus()) {
            log("Enabling bluetooth...")

            bluetoothManager.enableBluetooth(listener = object :
                BluetoothStateManager.OnBluetoothStatusListener {
                override fun onBluetoothTurnedOn() {
                    log("Bluetooth is on now")
                }

                override fun onError(throwable: Throwable) {
                    log(throwable.message ?: throwable.toString())
                }
            })
        } else {
            beaconAdvertiser.start(userUuid, advertiserCallback)
        }
    }

    private fun onDiscoverClick(v: View) {
        if (!bluetoothManager.getBluetoothStatus()) {
            log("Enabling bluetooth...")

            bluetoothManager.enableBluetooth(listener = object :
                BluetoothStateManager.OnBluetoothStatusListener {
                override fun onBluetoothTurnedOn() {
                    log("Bluetooth is on now")
                }

                override fun onError(throwable: Throwable) {
                    log(throwable.message ?: throwable.toString())
                }
            })
        } else {
            log("Start scanning...")
            beaconScanner.start(scannerCallback).observe(this, Observer { beacons ->
                binding.result.text = beacons.toString()
            })
        }
    }

    private fun log(message: String) {
        val date = Date()
        runOnUiThread {
            binding.log.text = /*"${dateFormat.format(date)} - " +*/
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
