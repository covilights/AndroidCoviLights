package com.covilights.beacon.advertiser

import androidx.lifecycle.LiveData
import com.covilights.beacon.BeaconCallback
import com.covilights.beacon.BeaconState

internal interface BeaconAdvertiser {

    val state: LiveData<BeaconState>

    fun start(callback: BeaconCallback? = null)
    fun stop(callback: BeaconCallback? = null)
}
