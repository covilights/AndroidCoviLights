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
