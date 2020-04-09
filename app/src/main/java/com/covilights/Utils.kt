package com.covilights

import android.os.ParcelUuid

fun String.toParcelUuid(): ParcelUuid? =
    ParcelUuid.fromString(this)
