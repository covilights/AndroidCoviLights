package com.covilights

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.text.TextUtils
import java.nio.charset.Charset

class ScanManager(private val listener: OnScanListener) {

    private val bluetoothLeScanner: BluetoothLeScanner =
        BluetoothAdapter.getDefaultAdapter().bluetoothLeScanner

    private val scanCallback: ScanCallback = object : ScanCallback() {

        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            if (result?.device == null || TextUtils.isEmpty(result.device.name)) return
            val builder =
                StringBuilder(result.device.name)
            builder.append("\n").append(
                result.scanRecord?.getServiceData(
                    result.scanRecord?.serviceUuids?.get(0)
                )?.let {
                    String(
                        it, Charset.forName("UTF-8")
                    )
                }
            )

            listener.onScanResult(builder.toString())
        }

        override fun onBatchScanResults(results: List<ScanResult?>?) {
            super.onBatchScanResults(results)
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            val error = getErrorCodeMessage(errorCode)
            listener.onError(Throwable("Discovery onScanFailed: $error"))
        }
    }

    fun startScan() {
        val filter: ScanFilter = ScanFilter.Builder()
            .setServiceUuid(Constants.pUuid)
            .build()
        val filters: List<ScanFilter> = listOf(filter)

        val settings: ScanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        bluetoothLeScanner.startScan(filters, settings, scanCallback)
    }

    private fun getErrorCodeMessage(errorCode: Int): String {
        return when (errorCode) {
            ScanCallback.SCAN_FAILED_ALREADY_STARTED -> "Fails to start scan as BLE scan with the same settings is already started by the app."
            ScanCallback.SCAN_FAILED_APPLICATION_REGISTRATION_FAILED -> "Fails to start scan as app cannot be registered."
            ScanCallback.SCAN_FAILED_INTERNAL_ERROR -> "Fails to start scan due an internal error"
            ScanCallback.SCAN_FAILED_FEATURE_UNSUPPORTED -> "Fails to start power optimized scan as this feature is not supported."
            SCAN_FAILED_OUT_OF_HARDWARE_RESOURCES -> "Fails to start scan as it is out of hardware resources."
            SCAN_FAILED_SCANNING_TOO_FREQUENTLY -> "Fails to start scan as application tries to scan too frequently."
            else -> "UNKNOWN"
        }
    }

    interface OnScanListener {

        fun onScanResult(result: String)

        fun onError(throwable: Throwable)
    }

    companion object {
        const val SCAN_FAILED_OUT_OF_HARDWARE_RESOURCES = 5
        const val SCAN_FAILED_SCANNING_TOO_FREQUENTLY = 6
    }
}
