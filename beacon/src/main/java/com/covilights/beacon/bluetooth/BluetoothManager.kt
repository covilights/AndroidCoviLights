package com.covilights.beacon.bluetooth

import androidx.lifecycle.LiveData
import com.covilights.beacon.BeaconCallback

internal interface BluetoothManager {

    val isEnabled: LiveData<Boolean>

    fun isBluetoothEnabled(): Boolean

    fun enableBluetooth(callback: BeaconCallback?)
}
