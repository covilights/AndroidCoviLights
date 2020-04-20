package com.covilights

import android.app.Application
import androidx.core.content.ContextCompat
import com.covilights.beacon.beaconModule
import com.covilights.injection.appModule
import com.covilights.service.BeaconServiceActions
import com.covilights.utils.StateManager
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class CoviLightsApplication : Application() {

    val stateManager: StateManager by inject()

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@CoviLightsApplication)
            modules(appModule, beaconModule)
        }

        if (!stateManager.isFirstRun) {
            ContextCompat.startForegroundService(applicationContext, BeaconServiceActions.AppStart.toIntent(applicationContext))
        }
    }
}
