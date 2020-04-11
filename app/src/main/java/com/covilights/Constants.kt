package com.covilights

import java.nio.ByteBuffer

object Constants {

    const val MANUFACTURER_UUID = "C0D7950D-73F1-4D4D-8E47-C090502D4497"
    const val GOOGLE_MANUFACTURER_ID = 224
    const val APPLE_MANUFACTURER_ID = 0x4c00

    const val BEACON_VISIBILITY_TIMEOUT: Long = 10_000

    fun getManufacturerData(): ByteArray {
        val manufacturerData: ByteBuffer = ByteBuffer.allocate(23)
        manufacturerData.put(0, 0x02.toByte()) // Beacon Identifier
        manufacturerData.put(1, 0x15.toByte()) // Beacon Identifier

        val uuid: ByteArray =
            Constants.MANUFACTURER_UUID.toUuid()?.toBytes() ?: byteArrayOf()
        for (i in 2..17) {
            manufacturerData.put(i, uuid[i - 2]) // adding the UUID
        }

        manufacturerData.put(18, 0x00.toByte()) // first byte of Major
        manufacturerData.put(19, 0x00.toByte()) // second byte of Major
        manufacturerData.put(20, 0x00.toByte()) // first minor
        manufacturerData.put(21, 0x00.toByte()) // second minor
        manufacturerData.put(22, 0xB5.toByte()) // txPower

        return manufacturerData.array()
    }

    fun getManufacturerDataMask(): ByteArray {
        val manufacturerDataMask = ByteBuffer.allocate(23)

        for (i in 0..17) {
            manufacturerDataMask.put(0x01.toByte())
        }

        return manufacturerDataMask.array()
    }
}
