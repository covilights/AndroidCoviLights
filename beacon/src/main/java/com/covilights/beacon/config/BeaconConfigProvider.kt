package com.covilights.beacon.config

interface BeaconConfigProvider {

    val userUuid: String
    val manufacturerId: Int
    val manufacturerUuid: String
}
