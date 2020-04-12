package com.covilights.beacon.utils

import android.os.ParcelUuid
import java.nio.ByteBuffer
import java.util.UUID

internal fun String.toParcelUuid(): ParcelUuid? =
    ParcelUuid.fromString(this)

internal fun String.toUuid(): UUID? =
    UUID.fromString(this)

internal fun UUID.toBytes(): ByteArray {
    val bb: ByteBuffer = ByteBuffer.wrap(ByteArray(16))
    bb.putLong(mostSignificantBits)
    bb.putLong(leastSignificantBits)
    return bb.array()
}

internal fun getManufacturerData(manufacturerUuid: String): ByteArray {
    val manufacturerData: ByteBuffer = ByteBuffer.allocate(23)
    manufacturerData.put(0, 0x02.toByte()) // Beacon Identifier
    manufacturerData.put(1, 0x15.toByte()) // Beacon Identifier

    val uuidBytes: ByteArray = manufacturerUuid.toUuid()?.toBytes() ?: byteArrayOf()
    for (i in 2..17) {
        manufacturerData.put(i, uuidBytes[i - 2]) // adding the UUID
    }

    manufacturerData.put(18, 0x00.toByte()) // first byte of Major
    manufacturerData.put(19, 0x00.toByte()) // second byte of Major
    manufacturerData.put(20, 0x00.toByte()) // first minor
    manufacturerData.put(21, 0x00.toByte()) // second minor
    manufacturerData.put(22, 0xB5.toByte()) // txPower

    return manufacturerData.array()
}

internal fun getManufacturerDataMask(): ByteArray {
    val manufacturerDataMask = ByteBuffer.allocate(23)

    for (i in 0..17) {
        manufacturerDataMask.put(0x01.toByte())
    }

    return manufacturerDataMask.array()
}