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

package com.covilights.service

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.os.bundleOf

sealed class BeaconServiceActions(private val serviceAction: String) {
    open val bundle: Bundle = bundleOf()

    object AppStart : BeaconServiceActions(ACTION_APP_START)

    object DeviceBoot : BeaconServiceActions(ACTION_DEVICE_BOOT)

    object StartBeacon : BeaconServiceActions(ACTION_START_BEACON)

    object StopBeacon : BeaconServiceActions(ACTION_STOP_BEACON)

    fun toIntent(context: Context): Intent =
        Intent(context, BeaconService::class.java).apply {
            action = serviceAction
            putExtras(bundle)
        }

    companion object {
        const val ACTION_APP_START = "beacon_service_app_start_action"
        const val ACTION_DEVICE_BOOT = "beacon_service_device_boot_action"
        const val ACTION_START_BEACON = "beacon_service_start_beacon_action"
        const val ACTION_STOP_BEACON = "beacon_service_stop_beacon_action"
    }
}
