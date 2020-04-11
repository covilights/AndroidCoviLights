package com.covilights.beacon.advertiser.utils

import android.bluetooth.le.AdvertisingSetCallback

fun getErrorCodeMessage(errorCode: Int): String {
    return when (errorCode) {
        AdvertisingSetCallback.ADVERTISE_SUCCESS -> "The requested advertising was successful."
        AdvertisingSetCallback.ADVERTISE_FAILED_DATA_TOO_LARGE -> "Failed to start advertising as the advertise data to be broadcasted is too large."
        AdvertisingSetCallback.ADVERTISE_FAILED_TOO_MANY_ADVERTISERS -> "Failed to start advertising because no advertising instance is available."
        AdvertisingSetCallback.ADVERTISE_FAILED_ALREADY_STARTED -> "Failed to start advertising as the advertising is already started."
        AdvertisingSetCallback.ADVERTISE_FAILED_INTERNAL_ERROR -> "Operation failed due to an internal error."
        AdvertisingSetCallback.ADVERTISE_FAILED_FEATURE_UNSUPPORTED -> "This feature is not supported on this platform."
        else -> "Unknown error!"
    }
}