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
