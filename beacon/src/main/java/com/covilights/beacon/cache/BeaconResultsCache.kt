package com.covilights.beacon.cache

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.covilights.beacon.model.Beacon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal class BeaconResultsCache {

    private val _results = MutableLiveData<Map<String, Beacon>>()
    val results: LiveData<Map<String, Beacon>>
        get() = _results

    init {
        _results.value = HashMap()

        GlobalScope.launch(Dispatchers.IO) {
            while (true) {
                val currentTime = System.currentTimeMillis()

                val value = _results.value
                value?.forEach {
                    if (it.value.lastSeen > currentTime - BEACON_VISIBILITY_TIMEOUT)
                        it.value.isVisible = false
                }
                _results.postValue(value)

                delay(BEACON_VISIBILITY_TIMEOUT)
            }
        }
    }

    fun add(beacon: Beacon) {
        val minDistanceInMeter = _results.value?.get(beacon.userUuid)?.minDistanceInMeter
        val beaconMinDistance: Beacon =
            if (minDistanceInMeter != null && minDistanceInMeter < beacon.minDistanceInMeter) beacon.copy(minDistanceInMeter = minDistanceInMeter)
            else beacon

        _results.value = _results.value?.toMutableMap()?.apply {
            put(beaconMinDistance.userUuid, beaconMinDistance)
        }
    }

    companion object {
        const val BEACON_VISIBILITY_TIMEOUT: Long = 10_000
    }
}
