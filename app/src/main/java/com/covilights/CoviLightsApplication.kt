package com.covilights

import android.app.Application
import androidx.core.content.ContextCompat
import com.covilights.beacon.beaconModule
import com.covilights.injection.appModule
import com.covilights.service.BeaconServiceActions
import com.covilights.view.main.mainModule
import com.covilights.view.onboarding.onboardingModule
import com.covilights.view.splash.splashModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class CoviLightsApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@CoviLightsApplication)
            modules(appModule, beaconModule, splashModule, onboardingModule, mainModule)
        }

        ContextCompat.startForegroundService(applicationContext, BeaconServiceActions.AppStart.toIntent(applicationContext))
    }
}
