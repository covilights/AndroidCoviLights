package com.covilights.beacon.scanner.model

import kotlin.math.pow

data class Beacon(
    val address: String,
    val userUuid: String,
    val rssi: Int,
    val txPower: Int,
    val lastSeen: Long
) {

    fun getDistanceInMeter(): Double {
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
}
