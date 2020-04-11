package com.covilights.beacon.scanner

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.os.Build
import android.os.ParcelUuid
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.covilights.Constants
import com.covilights.beacon.scanner.model.Beacon
import com.covilights.beacon.scanner.utils.getErrorCodeMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.nio.ByteBuffer

class BeaconScannerImpl : BeaconScanner {

    private val bluetoothAdapter: BluetoothAdapter
        get() = BluetoothAdapter.getDefaultAdapter()

    private val bluetoothScanner: BluetoothLeScanner
        get() = bluetoothAdapter.bluetoothLeScanner

    private val _state = MutableLiveData<BeaconScannerState>()
    override val state: LiveData<BeaconScannerState>
        get() = _state

    private val results = MutableLiveData<Map<String, Beacon>>()

    init {
        _state.value = BeaconScannerState.IDLE
        results.value = HashMap()


        GlobalScope.launch(Dispatchers.IO) {
            while (true) {
                val currentTime = System.currentTimeMillis()
                results.postValue(results.value?.filter {
                    it.value.lastSeen > currentTime - Constants.BEACON_VISIBILITY_TIMEOUT
                })

                delay(Constants.BEACON_VISIBILITY_TIMEOUT)
            }
        }
    }

    private var callback: BeaconScannerCallback? = null

    private val scanCallback: ScanCallback = object : ScanCallback() {

        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            if (result?.device == null) return

            val beacon = mapScanResultToBeacon(result)
            results.value = results.value?.toMutableMap()?.apply { put(beacon.address, beacon) }
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            val error = getErrorCodeMessage(errorCode)
            callback?.onError(Throwable("Discovery onScanFailed: $error"))
        }
    }

    private fun mapScanResultToBeacon(result: ScanResult): Beacon {
        val serviceUuids: List<ParcelUuid>? = result.scanRecord?.serviceUuids
        val userUuid: String? = if (serviceUuids != null && serviceUuids.isNotEmpty()) serviceUuids[0].toString() else null

        return Beacon(
            name = result.device.name,
            address = result.device.address,
            userUuid = userUuid,
            rssi = result.rssi,
            txPower = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) result.txPower else 0,
            lastSeen = System.currentTimeMillis()
        )
    }

    override fun start(callback: BeaconScannerCallback?): LiveData<Map<String, Beacon>> {

        if (_state.value == BeaconScannerState.RUNNING) {
            callback?.onSuccess()
            return results
        }

        this.callback = callback

        bluetoothScanner.startScan(getScanFilters(), getSettings(), scanCallback)

        _state.value = BeaconScannerState.RUNNING
        callback?.onSuccess()

        return results
    }

    private fun getScanFilters(): List<ScanFilter> {
        val filter: ScanFilter = ScanFilter.Builder()
            .setManufacturerData(Constants.APPLE_MANUFACTURER_ID, Constants.getManufacturerData(), getManufacturerDataMask())
            .build()
        return listOf(filter)
    }

    private fun getManufacturerDataMask(): ByteArray {
        val manufacturerDataMask = ByteBuffer.allocate(23)

        for (i in 0..17) {
            manufacturerDataMask.put(0x01.toByte())
        }

        return manufacturerDataMask.array()
    }

    private fun getSettings(): ScanSettings? {
        val builder = ScanSettings.Builder()
        // builder.setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
        builder.setScanMode(ScanSettings.SCAN_MODE_BALANCED)
        builder.setReportDelay(0)

        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        // builder.setLegacy(false)
        // builder.setPhy(ScanSettings.PHY_LE_ALL_SUPPORTED)
        // }

        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        //     builder.setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
        //     builder.setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
        //     builder.setNumOfMatches(ScanSettings.MATCH_NUM_MAX_ADVERTISEMENT)
        // }

        return builder.build()
    }

    override fun stop(callback: BeaconScannerCallback?) {
        if (_state.value == BeaconScannerState.IDLE) {
            callback?.onSuccess()
            return
        }

        bluetoothScanner.stopScan(scanCallback)
    }
}
