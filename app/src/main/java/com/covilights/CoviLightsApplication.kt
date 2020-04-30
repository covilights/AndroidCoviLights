/*
 * Copyright 2020 CoviLights GbR
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.covilights

import android.app.Application
import androidx.core.content.ContextCompat
import com.covilights.injection.appModule
import com.covilights.service.BeaconServiceActions
import com.covilights.utils.StateManager
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

/**
 * CoviLights Application class which starts Koin and the BeaconService foreground if it is not the first run before onboarding.
 */
class CoviLightsApplication : Application() {

    private val stateManager: StateManager by inject()

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@CoviLightsApplication)

            modules(appModule)
        }

        if (!stateManager.isFirstRun) {
            ContextCompat.startForegroundService(applicationContext, BeaconServiceActions.AppStart.toIntent(applicationContext))
        }
    }
}
