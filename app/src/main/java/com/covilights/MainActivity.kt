package com.covilights

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.RemoteException
import android.text.method.ScrollingMovementMethod
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.covilights.databinding.MainActivityBinding
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.BeaconConsumer
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.BeaconParser
import org.altbeacon.beacon.BeaconTransmitter
import org.altbeacon.beacon.MonitorNotifier
import org.altbeacon.beacon.Region
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity(), BeaconConsumer {

    lateinit var binding: MainActivityBinding

    lateinit var beaconManager: BeaconManager

    private val dateFormat = SimpleDateFormat("hh:mm:ss", Locale.US)

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
        val beacon = Beacon.Builder()
            .setId1("2f234454-cf6d-4a0f-adf2-f4911ba9ffa6")
            .setId2("1")
            .setId3("2")
            .setManufacturer(0x0118)
            .setTxPower(-59)
            .setDataFields(listOf(0L))
            .build()
        val beaconParser = BeaconParser()
            .setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25")
        val beaconTransmitter =
            BeaconTransmitter(applicationContext, beaconParser)
        beaconTransmitter.startAdvertising(beacon)
    }

    private fun onDiscoverClick(v: View) {
        beaconManager = BeaconManager.getInstanceForApplication(this);
        // To detect proprietary beacons, you must add a line like below corresponding to your beacon
        // type.  Do a web search for "setBeaconLayout" to get the proper expression.
        // beaconManager.getBeaconParsers().add(new BeaconParser().
        //        setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        beaconManager.bind(this);
    }

    override fun onDestroy() {
        super.onDestroy()
        beaconManager.unbind(this)
    }

    override fun onBeaconServiceConnect() {
        monitoring()
        ranging()
    }

    private fun ranging() {
        beaconManager.removeAllRangeNotifiers()
        beaconManager.addRangeNotifier { beacons, region ->
            if (beacons.isNotEmpty()) {
                log(
                    "The first beacon I see is about " + beacons.iterator().next()
                        .distance + " meters away."
                )
            }
        }

        try {
            beaconManager.startRangingBeaconsInRegion(
                Region("myRangingUniqueId", null, null, null)
            )
        } catch (e: RemoteException) {
        }
    }

    private fun monitoring() {
        beaconManager.removeAllMonitorNotifiers()
        beaconManager.addMonitorNotifier(object : MonitorNotifier {
            override fun didEnterRegion(region: Region) {
                log("I just saw an beacon for the first time!")
            }

            override fun didExitRegion(region: Region) {
                log("I no longer see an beacon")
            }

            override fun didDetermineStateForRegion(
                state: Int,
                region: Region
            ) {
                log("I have just switched from seeing/not seeing beacons: $state")
            }
        })

        try {
            beaconManager.startMonitoringBeaconsInRegion(
                Region("myMonitoringUniqueId", null, null, null)
            )
        } catch (e: RemoteException) {
        }
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
