package com.covilights.beacon

import androidx.lifecycle.LiveData
import com.covilights.beacon.model.Beacon

interface BeaconManager {

    val state: LiveData<BeaconState>

    val results: LiveData<Map<String, Beacon>>

    fun hasBleFeature(): Boolean

    fun isBluetoothEnabled(): Boolean

    fun enableBluetooth(callback: BeaconCallback? = null)

    fun start(callback: BeaconCallback? = null)
    fun stop(callback: BeaconCallback? = null)
}