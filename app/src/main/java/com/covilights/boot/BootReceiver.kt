package com.covilights.boot

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.covilights.service.BeaconServiceActions

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            if (context != null) ContextCompat.startForegroundService(context, BeaconServiceActions.DeviceBoot.toIntent(context))
        }
    }
}
