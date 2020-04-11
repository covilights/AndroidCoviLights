package com.covilights.beacon.advertiser

import androidx.lifecycle.LiveData

interface BeaconAdvertiser {

    val state: LiveData<BeaconAdvertiserState>

    fun start(userUuid: String, callback: BeaconAdvertiserCallback? = null)
    fun stop(callback: BeaconAdvertiserCallback? = null)
}