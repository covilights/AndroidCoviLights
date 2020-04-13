package com.covilights.user

import android.content.Context
import com.covilights.DebugActivity
import com.mirhoseini.appsettings.AppSettings
import java.util.UUID

class UserManager(context: Context) {

    val userUuid: String by lazy {
        AppSettings.getString(context, DebugActivity.USER_UUID) ?: run {
            val newUuid = UUID.randomUUID().toString()
            AppSettings.setValue(context, DebugActivity.USER_UUID, newUuid)
            return@run newUuid
        }
    }
}
