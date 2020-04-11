package com.covilights.beacon.scanner.utils

import android.bluetooth.le.ScanCallback

fun getErrorCodeMessage(errorCode: Int): String {
    return when (errorCode) {
        NO_ERROR -> "The requested scanning was successful."
        ScanCallback.SCAN_FAILED_ALREADY_STARTED -> "Fails to start scan as BLE scan with the same settings is already started by the app."
        ScanCallback.SCAN_FAILED_APPLICATION_REGISTRATION_FAILED -> "Fails to start scan as app cannot be registered."
        ScanCallback.SCAN_FAILED_INTERNAL_ERROR -> "Fails to start scan due an internal error"
        ScanCallback.SCAN_FAILED_FEATURE_UNSUPPORTED -> "Fails to start power optimized scan as this feature is not supported."
        SCAN_FAILED_OUT_OF_HARDWARE_RESOURCES -> "Fails to start scan as it is out of hardware resources."
        SCAN_FAILED_SCANNING_TOO_FREQUENTLY -> "Fails to start scan as application tries to scan too frequently."
        else -> "Unknown error!"
    }
}

const val NO_ERROR = 0
const val SCAN_FAILED_OUT_OF_HARDWARE_RESOURCES = 5
const val SCAN_FAILED_SCANNING_TOO_FREQUENTLY = 6
