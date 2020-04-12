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
import com.covilights.beacon.BeaconCallback
import com.covilights.beacon.BeaconState
import com.covilights.beacon.cache.BeaconResultsCache
import com.covilights.beacon.config.BeaconConfigProvider
import com.covilights.beacon.model.Beacon
import com.covilights.beacon.utils.getManufacturerData
import com.covilights.beacon.utils.getManufacturerDataMask

internal class BeaconScannerImpl(private val config: BeaconConfigProvider, private val resultsCache: BeaconResultsCache) : BeaconScanner {

    private val bluetoothAdapter: BluetoothAdapter
        get() = BluetoothAdapter.getDefaultAdapter()

    private val bluetoothScanner: BluetoothLeScanner
        get() = bluetoothAdapter.bluetoothLeScanner

    private val _state = MutableLiveData<BeaconState>()
    override val state: LiveData<BeaconState>
        get() = _state

    init {
        _state.value = BeaconState.STOPPED
    }

    private var callback: BeaconCallback? = null

    private val scanCallback = object : ScanCallback() {

        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            val userUuid = result?.getUserUuid()
            if (result == null || result.device == null || userUuid == null) return
            resultsCache.add(mapScanResultToBeacon(result, userUuid))
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

    override fun start(callback: BeaconCallback?) {

        if (_state.value == BeaconState.STARTED) {
            callback?.onSuccess()
            return
        }

        this.callback = callback

        bluetoothScanner.startScan(getScanFilters(), getSettings(), scanCallback)

        _state.value = BeaconState.STARTED
        callback?.onSuccess()
    }

    private fun getScanFilters(): List<ScanFilter> {
        return listOf(
            ScanFilter.Builder().apply {
                setManufacturerData(config.manufacturerId, getManufacturerData(config.manufacturerUuid), getManufacturerDataMask())
            }.build()
        )
    }

    private fun getSettings(): ScanSettings? {
        return ScanSettings.Builder().apply {
            setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
            setReportDelay(0)
        }.build()
    }

    override fun stop(callback: BeaconCallback?) {
        if (_state.value == BeaconState.STOPPED) {
            callback?.onSuccess()
            return
        }

        bluetoothScanner.stopScan(scanCallback)

        _state.value = BeaconState.STOPPED
        callback?.onSuccess()
    }

    private fun getErrorCodeMessage(errorCode: Int): String {
        return when (errorCode) {
            NO_ERROR ->
                "The requested scanning was successful."
            ScanCallback.SCAN_FAILED_ALREADY_STARTED ->
                "Fails to start scan as BLE scan with the same settings is already started by the app."
            ScanCallback.SCAN_FAILED_APPLICATION_REGISTRATION_FAILED ->
                "Fails to start scan as app cannot be registered."
            ScanCallback.SCAN_FAILED_INTERNAL_ERROR ->
                "Fails to start scan due an internal error"
            ScanCallback.SCAN_FAILED_FEATURE_UNSUPPORTED ->
                "Fails to start power optimized scan as this feature is not supported."
            SCAN_FAILED_OUT_OF_HARDWARE_RESOURCES ->
                "Fails to start scan as it is out of hardware resources."
            SCAN_FAILED_SCANNING_TOO_FREQUENTLY ->
                "Fails to start scan as application tries to scan too frequently."
            else ->
                "Unknown error!"
        }
    }

    companion object {
        const val NO_ERROR = 0
        const val SCAN_FAILED_OUT_OF_HARDWARE_RESOURCES = 5
        const val SCAN_FAILED_SCANNING_TOO_FREQUENTLY = 6
    }
}
