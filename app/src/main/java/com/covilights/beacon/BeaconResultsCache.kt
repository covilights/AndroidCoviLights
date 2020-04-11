package com.covilights.beacon

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.covilights.Constants
import com.covilights.beacon.scanner.model.Beacon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BeaconResultsCache {

    private val _results = MutableLiveData<Map<String, Beacon>>()
    val results: LiveData<Map<String, Beacon>>
        get() = _results

    init {
        _results.value = HashMap()

        GlobalScope.launch(Dispatchers.IO) {
            while (true) {
                val currentTime = System.currentTimeMillis()
                _results.postValue(results.value?.filter {
                    it.value.lastSeen > currentTime - Constants.BEACON_VISIBILITY_TIMEOUT
                })

                delay(Constants.BEACON_VISIBILITY_TIMEOUT)
            }
        }
    }

    fun add(beacon: Beacon) {
        _results.value = _results.value?.toMutableMap()?.apply { put(beacon.userUuid, beacon) }
    }
}