package com.covilights.beacon.scanner

import androidx.lifecycle.LiveData
import com.covilights.beacon.BeaconCallback
import com.covilights.beacon.BeaconState

internal interface BeaconScanner {

    val state: LiveData<BeaconState>

    fun start(callback: BeaconCallback? = null)

    fun stop(callback: BeaconCallback? = null)
}
