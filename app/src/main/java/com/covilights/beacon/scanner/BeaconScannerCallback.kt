package com.covilights.beacon.scanner

interface BeaconScannerCallback {

    fun onSuccess()

    fun onError(throwable: Throwable)
}