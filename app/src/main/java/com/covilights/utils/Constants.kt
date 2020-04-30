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

package com.covilights.utils

import com.mohsenoid.closetome.utils.toUuid
import java.util.UUID

/**
 * Constant variables used widely in the app.
 */
object Constants {

    const val SPLASH_TIMEOUT = 2_000L

    const val NOTIFICATION_CHANNEL_ID = "beacon_service"
    const val NOTIFICATION_CHANNEL_NAME = "Beacon Service Channel"
    const val NOTIFICATION_CHANNEL_DESC = "Beacon background service"
    const val NOTIFICATION_ID = 1

    val MANUFACTURER_UUID: UUID = "C0D7950D-73F1-4D4D-8E47-C090502D4497".toUuid()!!

    const val VISIBILITY_TIMEOUT: Long = 10_000
    const val VISIBILITY_DISTANCE: Double = 1.0
}
