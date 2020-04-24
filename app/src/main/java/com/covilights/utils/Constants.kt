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

package com.covilights.utils

object Constants {

    const val SPLASH_TIMEOUT = 2_000L

    const val NOTIFICATION_CHANNEL_ID = "beacon_service"
    const val NOTIFICATION_CHANNEL_NAME = "Beacon Service Channel"
    const val NOTIFICATION_CHANNEL_DESC = "Beacon background service"
    const val NOTIFICATION_ID = 1

    const val MANUFACTURER_UUID = "C0D7950D-73F1-4D4D-8E47-C090502D4497"
    const val GOOGLE_MANUFACTURER_ID = 224
    const val APPLE_MANUFACTURER_ID = 0x4c00

    const val BEACON_VISIBILITY_TIMEOUT: Long = 10_000
    const val BEACON_VISIBILITY_DISTANCE: Double = 1.0
}
