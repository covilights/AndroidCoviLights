package com.covilights.beacon.scanner

import androidx.lifecycle.LiveData
import com.covilights.beacon.scanner.model.Beacon

interface BeaconScanner {

    val state: LiveData<BeaconScannerState>

    fun start(callback: BeaconScannerCallback? = null): LiveData<HashMap<String, Beacon>>
    fun stop(callback: BeaconScannerCallback? = null)
}