package com.covilights.beacon.advertiser

interface BeaconAdvertiserCallback {

    fun onSuccess()

    fun onError(throwable: Throwable)
}