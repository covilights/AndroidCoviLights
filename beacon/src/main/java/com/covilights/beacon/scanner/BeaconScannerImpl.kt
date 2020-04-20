/*
 * Copyright 2020 CoviLights Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import kotlin.math.pow

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
        val txPower = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) result.txPower else 0
        val distance = calculateDistanceInMeter(result.rssi, txPower)

        return Beacon(
            address = result.device.address,
            userUuid = userUuid,
            lastSeen = System.currentTimeMillis(),
            distanceInMeter = distance,
            isNear = distance < config.visibilityDistance,
            isVisible = true
        )
    }

    private fun calculateDistanceInMeter(rssi: Int, txPower: Int): Double {
        if (rssi == 0) {
            return -1.0 // if we cannot determine accuracy, return -1.
        }
        val ratio = rssi * 1.0 / txPower
        return if (ratio < 1.0) {
            ratio.pow(10.0)
        } else {
            0.89976 * ratio.pow(7.7095) + 0.111
        } * 100 // fix to show correct meter!
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
