package com.covilights.beacon.scanner

import androidx.lifecycle.LiveData

interface BeaconScanner {

    val state: LiveData<BeaconScannerState>

    fun start(callback: BeaconScannerCallback? = null)

    fun stop(callback: BeaconScannerCallback? = null)
}