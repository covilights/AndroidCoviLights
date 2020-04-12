package com.covilights.beacon

interface BeaconCallback {

    fun onSuccess()

    fun onError(throwable: Throwable)
}
