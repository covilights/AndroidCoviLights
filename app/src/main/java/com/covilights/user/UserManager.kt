package com.covilights.user

import android.content.Context
import com.covilights.MainActivity
import com.mirhoseini.appsettings.AppSettings
import java.util.UUID

class UserManager(context: Context) {

    val userUuid: String by lazy {
        AppSettings.getString(context, MainActivity.USER_UUID) ?: run {
            val newUuid = UUID.randomUUID().toString()
            AppSettings.setValue(context, MainActivity.USER_UUID, newUuid)
            return@run newUuid
        }
    }
}
