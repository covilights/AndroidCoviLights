/*
 * Copyright 2020 CoviLights GbR
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.covilights.beacon

import android.content.Context
import android.content.pm.PackageManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import com.covilights.beacon.advertiser.BeaconAdvertiser
import com.covilights.beacon.bluetooth.BluetoothManager
import com.covilights.beacon.cache.BeaconResultsCache
import com.covilights.beacon.model.Beacon
import com.covilights.beacon.scanner.BeaconScanner
import com.covilights.beacon.utils.CombinedLiveData

internal class BeaconManagerImpl(
    private val context: Context,
    private val bluetoothManager: BluetoothManager,
    private val advertiser: BeaconAdvertiser,
    private val scanner: BeaconScanner,
    private val cache: BeaconResultsCache
) : BeaconManager {

    override val state: MediatorLiveData<BeaconState> =
        CombinedLiveData(scanner.state, advertiser.state) { scannerState, advertiserState, currentValue ->
            when {
                scannerState == null || advertiserState == null -> null
                scannerState == advertiserState -> scannerState
                else -> BeaconState.STOPPED
            }?.takeIf { newValue ->
                currentValue != newValue
            }
        }

    override val results: LiveData<Map<String, Beacon>>
        get() = cache.results

    init {
        bluetoothManager.isEnabled.observeForever(Observer { isActive ->
            if (isActive == false) {
                stop()
            } else {
                start()
            }
        })
    }

    override fun hasBleFeature(): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)
    }

    override fun isBluetoothEnabled(): Boolean {
        return bluetoothManager.isEnabled.value ?: false
    }

    override fun enableBluetooth(callback: BeaconCallback?) {
        bluetoothManager.enableBluetooth(callback)
    }

    override fun start(callback: BeaconCallback?) {
        if (!bluetoothManager.isBluetoothEnabled()) {
            callback?.onError(Throwable("Bluetooth is not enable!"))
            return
        }

        advertiser.start(object : BeaconCallback {
            override fun onSuccess() {
                scanner.start(object : BeaconCallback {
                    override fun onSuccess() {
                        callback?.onSuccess()
                    }

                    override fun onError(throwable: Throwable) {
                        callback?.onError(throwable)
                    }
                })
            }

            override fun onError(throwable: Throwable) {
                callback?.onError(throwable)
            }
        })
    }

    override fun stop(callback: BeaconCallback?) {
        advertiser.stop(object : BeaconCallback {
            override fun onSuccess() {
                scanner.stop(object : BeaconCallback {
                    override fun onSuccess() {
                        callback?.onSuccess()
                    }

                    override fun onError(throwable: Throwable) {
                        callback?.onError(throwable)
                    }
                })
            }

            override fun onError(throwable: Throwable) {
                callback?.onError(throwable)
            }
        })
    }
}
