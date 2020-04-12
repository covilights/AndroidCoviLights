package com.covilights

object Constants {

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
