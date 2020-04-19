package com.covilights.user

import android.content.Context
import com.mirhoseini.appsettings.AppSettings
import java.util.UUID

class UserManager(val context: Context) {

    val userUuid: String by lazy {
        AppSettings.getString(context, USER_UUID) ?: run {
            val newUuid = UUID.randomUUID().toString()
            AppSettings.setValue(context, USER_UUID, newUuid)
            return@run newUuid
        }
    }

    var userStatus: UserStatus
        get() = UserStatus.values()[AppSettings.getInt(context, USER_STATUS) ?: 0]
        set(value) {
            AppSettings.setValue(context, USER_STATUS, value.ordinal)
        }

    companion object {
        const val USER_UUID = "user_uuid"
        const val USER_STATUS = "user_status"
    }
}
