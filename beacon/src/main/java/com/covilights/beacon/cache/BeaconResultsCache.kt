package com.covilights.beacon.cache

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.covilights.beacon.config.BeaconConfigProvider
import com.covilights.beacon.model.Beacon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.min

internal class BeaconResultsCache(config: BeaconConfigProvider) {

    private val _results = MutableLiveData<Map<String, Beacon>>()
    val results: LiveData<Map<String, Beacon>>
        get() = _results

    init {
        _results.value = HashMap()

        GlobalScope.launch(Dispatchers.IO) {
            while (true) {
                val value = _results.value
                value?.filter { it.value.isVisible }?.forEach {
                    if (it.value.lastSeen < System.currentTimeMillis() - config.visibilityTimeout) {
                        it.value.isVisible = false
                    }
                }
                _results.postValue(value)

                delay(config.visibilityTimeout)
            }
        }
    }

    fun add(beacon: Beacon) {
        val beaconWithMinDistance = updateBeaconMinDistance(beacon)
        addResult(beaconWithMinDistance)
    }

    private fun updateBeaconMinDistance(beacon: Beacon): Beacon {
        return _results.value?.get(beacon.userUuid)?.let { existingBeacon ->
            beacon.copy(minDistanceInMeter = min(existingBeacon.minDistanceInMeter, beacon.distanceInMeter))
        } ?: beacon.copy(minDistanceInMeter = beacon.distanceInMeter)
    }

    private fun addResult(beacon: Beacon) {
        _results.value = _results.value?.toMutableMap()?.apply {
            put(beacon.userUuid, beacon)
        }
    }
}
