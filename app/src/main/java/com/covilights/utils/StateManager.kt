package com.covilights.utils

import android.content.Context
import com.mirhoseini.appsettings.AppSettings

class StateManager(val context: Context) {

    var isFirstRun: Boolean
        get() = AppSettings.getBoolean(context, IS_FIRST_RUN) ?: true
        set(value) {
            AppSettings.setValue(context, IS_FIRST_RUN, value)
        }

    companion object {
        const val IS_FIRST_RUN = "is_first_run"
    }
}
