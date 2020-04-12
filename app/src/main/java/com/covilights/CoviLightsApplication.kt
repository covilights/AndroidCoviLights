package com.covilights

import android.app.Application
import com.covilights.beacon.beaconModule
import com.covilights.injection.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class CoviLightsApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@CoviLightsApplication)
            modules(appModule, beaconModule)
        }
    }
}
