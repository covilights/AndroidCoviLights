package com.covilights

import android.os.ParcelUuid
import java.nio.ByteBuffer
import java.util.UUID

fun String.toParcelUuid(): ParcelUuid? =
    ParcelUuid.fromString(this)

fun String.toUuid(): UUID? =
    UUID.fromString(this)

fun UUID.toBytes(): ByteArray {
    val bb: ByteBuffer = ByteBuffer.wrap(ByteArray(16))
    bb.putLong(mostSignificantBits)
    bb.putLong(leastSignificantBits)
    return bb.array()
}

fun UUID.toShortBytesLeast(): ByteArray {
    val bb: ByteBuffer = ByteBuffer.wrap(ByteArray(8))
    bb.putLong(leastSignificantBits)
    return bb.array()
}

fun UUID.toShortBytesMost(): ByteArray {
    val bb: ByteBuffer = ByteBuffer.wrap(ByteArray(8))
    bb.putLong(mostSignificantBits)
    return bb.array()
}

fun ByteArray.toUuid(): UUID {
    val byteBuffer = ByteBuffer.wrap(this)
    val high = byteBuffer.long
    val low = byteBuffer.long
    return UUID(high, low)
}
