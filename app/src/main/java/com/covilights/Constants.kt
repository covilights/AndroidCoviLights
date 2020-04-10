package com.covilights

import java.nio.ByteBuffer
import java.util.UUID

object Constants {

    const val MANUFACTURER_UUID = "C0D7950D-73F1-4D4D-8E47-C090502D4497"

    const val SERVICE_UUID_MOST = "C0D7950D-73F1-4D4D-8E47-C090502D4498"
    const val SERVICE_UUID_LEAST = "C0D7950D-73F1-4D4D-8E47-C090502D4499"

    const val MANUFACTURER_ID = 224 // using google's company ID

    fun getBeaconManufacturerData(): ByteArray {
        val manufacturerData: ByteBuffer = ByteBuffer.allocate(24)
        manufacturerData.put(0, 0xBE.toByte()) // Beacon Identifier
        manufacturerData.put(1, 0xAC.toByte()) // Beacon Identifier

        val uuid: ByteArray =
            MANUFACTURER_UUID.toUuid()?.toBytes() ?: byteArrayOf()
        for (i in 2..17) {
            manufacturerData.put(i, uuid[i - 2]) // adding the UUID
        }

        manufacturerData.put(18, 0x00.toByte()) // first byte of Major
        manufacturerData.put(19, 0x09.toByte()) // second byte of Major
        manufacturerData.put(20, 0x00.toByte()) // first minor
        manufacturerData.put(21, 0x06.toByte()) // second minor
        manufacturerData.put(22, 0xB5.toByte()) // txPower
        return manufacturerData.array()
    }
}
