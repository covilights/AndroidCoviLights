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
import com.covilights.beacon.BeaconResultsCache
import com.covilights.beacon.scanner.model.Beacon
import com.covilights.beacon.scanner.utils.getErrorCodeMessage

class BeaconScannerImpl(val beaconResultsCache: BeaconResultsCache) : BeaconScanner {

    private val bluetoothAdapter: BluetoothAdapter
        get() = BluetoothAdapter.getDefaultAdapter()

    private val bluetoothScanner: BluetoothLeScanner
        get() = bluetoothAdapter.bluetoothLeScanner

    private val _state = MutableLiveData<BeaconScannerState>()
    override val state: LiveData<BeaconScannerState>
        get() = _state

    init {
        _state.value = BeaconScannerState.IDLE
    }

    private var callback: BeaconScannerCallback? = null

    private val scanCallback: ScanCallback = object : ScanCallback() {

        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            val userUuid = result?.getUserUuid()
            if (result == null || result.device == null || userUuid == null) return
            beaconResultsCache.add(mapScanResultToBeacon(result, userUuid))
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            val error = getErrorCodeMessage(errorCode)
            callback?.onError(Throwable("Discovery onScanFailed: $error"))
        }
    }

    private fun mapScanResultToBeacon(result: ScanResult, userUuid: String): Beacon {
        return Beacon(
            address = result.device.address,
            userUuid = userUuid,
            rssi = result.rssi,
            txPower = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) result.txPower else 0,
            lastSeen = System.currentTimeMillis()
        )
    }

    private fun ScanResult.getUserUuid(): String? {
        val serviceUuids: List<ParcelUuid>? = scanRecord?.serviceUuids
        return if (serviceUuids != null && serviceUuids.isNotEmpty()) serviceUuids[0].toString() else null
    }

    override fun start(callback: BeaconScannerCallback?) {

        if (_state.value == BeaconScannerState.RUNNING) {
            callback?.onSuccess()
            return
        }

        this.callback = callback

        bluetoothScanner.startScan(getScanFilters(), getSettings(), scanCallback)

        _state.value = BeaconScannerState.RUNNING
        callback?.onSuccess()
    }

    private fun getScanFilters(): List<ScanFilter> {
        return listOf(
            ScanFilter.Builder().apply {
                setManufacturerData(Constants.APPLE_MANUFACTURER_ID, Constants.getManufacturerData(), Constants.getManufacturerDataMask())
            }.build()
        )
    }

    private fun getSettings(): ScanSettings? {
        return ScanSettings.Builder().apply {
            setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
            setReportDelay(0)
        }.build()
    }

    override fun stop(callback: BeaconScannerCallback?) {
        if (_state.value == BeaconScannerState.IDLE) {
            callback?.onSuccess()
            return
        }

        bluetoothScanner.stopScan(scanCallback)
    }
}
