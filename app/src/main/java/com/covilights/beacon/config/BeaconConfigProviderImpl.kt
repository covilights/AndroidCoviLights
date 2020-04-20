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

package com.covilights.beacon.config

import com.covilights.user.UserManager
import com.covilights.utils.Constants

class BeaconConfigProviderImpl(private val userManager: UserManager) : BeaconConfigProvider {

    override val userUuid: String
        get() = userManager.userUuid

    override val manufacturerId: Int
        get() = Constants.APPLE_MANUFACTURER_ID

    override val manufacturerUuid: String
        get() = Constants.MANUFACTURER_UUID

    override val visibilityTimeout: Long
        get() = Constants.BEACON_VISIBILITY_TIMEOUT

    override val visibilityDistance: Double
        get() = Constants.BEACON_VISIBILITY_DISTANCE
}
