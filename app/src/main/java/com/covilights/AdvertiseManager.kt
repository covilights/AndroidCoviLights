package com.covilights

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.AdvertisingSetCallback

class AdvertiseManager(private val listener: OnAdvertiseListener) {

    private val advertisingCallback: AdvertiseCallback = object : AdvertiseCallback() {

        override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
            listener.onSuccess()

            super.onStartSuccess(settingsInEffect)
        }

        override fun onStartFailure(errorCode: Int) {
            val error = getErrorCodeMessage(errorCode)

            listener.onError(Throwable("Advertising onStartFailure: $errorCode - $error"))

            super.onStartFailure(errorCode)
        }
    }

    fun startAdvertising() {

        val settings = AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
            .setConnectable(false)
            .build()

        val data: AdvertiseData = AdvertiseData.Builder()
            .setIncludeDeviceName(false)
            .addServiceData(Constants.pUuid, "It is me!".toByteArray(Charsets.UTF_8))
            .build()

        val advertiser = BluetoothAdapter.getDefaultAdapter().bluetoothLeAdvertiser

        advertiser.startAdvertising(settings, data, advertisingCallback)
    }

    private fun getErrorCodeMessage(errorCode: Int): String {
        return when (errorCode) {
            AdvertisingSetCallback.ADVERTISE_SUCCESS -> "ADVERTISE_SUCCESS"
            AdvertisingSetCallback.ADVERTISE_FAILED_DATA_TOO_LARGE -> "ADVERTISE_FAILED_DATA_TOO_LARGE"
            AdvertisingSetCallback.ADVERTISE_FAILED_TOO_MANY_ADVERTISERS -> "ADVERTISE_FAILED_TOO_MANY_ADVERTISERS"
            AdvertisingSetCallback.ADVERTISE_FAILED_ALREADY_STARTED -> "ADVERTISE_FAILED_ALREADY_STARTED"
            AdvertisingSetCallback.ADVERTISE_FAILED_INTERNAL_ERROR -> "ADVERTISE_FAILED_INTERNAL_ERROR"
            AdvertisingSetCallback.ADVERTISE_FAILED_FEATURE_UNSUPPORTED -> "ADVERTISE_FAILED_FEATURE_UNSUPPORTED"
            else -> "UNKNOWN"
        }
    }

    interface OnAdvertiseListener {

        fun onSuccess()

        fun onError(throwable: Throwable)
    }
}
