package com.covilights.beacon.config

import com.covilights.Constants
import com.covilights.user.UserManager

class BeaconConfigProviderImpl(private val userManager: UserManager) : BeaconConfigProvider {

    override val userUuid: String
        get() = userManager.userUuid

    override val manufacturerId: Int
        get() = Constants.APPLE_MANUFACTURER_ID

    override val manufacturerUuid: String
        get() = Constants.MANUFACTURER_UUID
}