package com.covilights.beacon.scanner.model

import java.util.UUID
import kotlin.math.pow

data class Beacon(
    val name: String?,
    val address: String,
    val userUuid: UUID?,
    val rssi: Int,
    val txPower: Int
) {

    val calculateDistance: Double
        get() {
            if (rssi == 0) {
                return -1.0 // if we cannot determine accuracy, return -1.
            }
            val ratio = rssi * 1.0 / txPower
            return if (ratio < 1.0) {
                ratio.pow(10.0)
            } else {
                0.89976 * ratio.pow(7.7095) + 0.111
            }
        }

    override fun toString(): String {
        return "content: $userUuid\ndistance:$calculateDistance"
    }
}