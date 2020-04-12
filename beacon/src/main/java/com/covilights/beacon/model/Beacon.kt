package com.covilights.beacon.model

data class Beacon(
    val address: String,
    val userUuid: String,
    val lastSeen: Long,
    val distanceInMeter: Double,
    val minDistanceInMeter: Double = Double.MAX_VALUE,
    val isNear: Boolean,
    var isVisible: Boolean
)
