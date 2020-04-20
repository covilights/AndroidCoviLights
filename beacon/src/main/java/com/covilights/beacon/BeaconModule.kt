/*
 * Copyright 2020 CoviLights Team
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

import com.covilights.beacon.advertiser.BeaconAdvertiser
import com.covilights.beacon.advertiser.BeaconAdvertiserImpl
import com.covilights.beacon.bluetooth.BluetoothManager
import com.covilights.beacon.bluetooth.BluetoothManagerImpl
import com.covilights.beacon.cache.BeaconResultsCache
import com.covilights.beacon.scanner.BeaconScanner
import com.covilights.beacon.scanner.BeaconScannerImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val beaconModule = module {

    single { BeaconResultsCache(config = get()) }

    single<BluetoothManager> { BluetoothManagerImpl(context = androidContext()) }

    single<BeaconAdvertiser> { BeaconAdvertiserImpl(config = get()) }

    single<BeaconScanner> { BeaconScannerImpl(config = get(), resultsCache = get()) }

    single<BeaconManager> {
        BeaconManagerImpl(
            context = androidContext(),
            bluetoothManager = get(),
            advertiser = get(),
            scanner = get(),
            cache = get()
        )
    }
}
