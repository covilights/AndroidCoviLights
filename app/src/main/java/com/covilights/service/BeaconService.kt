/*
 * Copyright 2020 CoviLights GbR
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.covilights.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.Observer
import com.covilights.R
import com.covilights.utils.Constants
import com.covilights.view.MainActivity
import com.mohsenoid.closetome.CloseToMe
import com.mohsenoid.closetome.CloseToMeCallback
import com.mohsenoid.closetome.CloseToMeState
import org.koin.android.ext.android.inject
import timber.log.Timber

/**
 * Beacon Android Service which keeps the CloseToMe feature running in background.
 */
class BeaconService : LifecycleService() {

    private val closeToMe: CloseToMe by inject()

    private var notificationBuilder: NotificationCompat.Builder? = null

    override fun onCreate() {
        super.onCreate()

        closeToMe.state.observe(this, Observer { state ->
            notificationBuilder?.updateNotificationWithState(state)?.also {
                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(Constants.NOTIFICATION_ID, it.build())

                if (state != CloseToMeState.STARTED) ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_DETACH)
            }
        })

        closeToMe.results.observe(this, Observer { result ->
            val resultCount =
                result.values.filter { it.isVisible && it.isNear }.size
            notificationBuilder?.applyNotificationResultCount(resultCount)?.also {
                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(Constants.NOTIFICATION_ID, it.build())
            }
        })

        closeToMe.start(object : CloseToMeCallback {
            override fun onSuccess() {
                Timber.i("CoviLights started successfully.")
            }

            override fun onError(throwable: Throwable) {
                Timber.w(throwable, "CoviLights didn't start.")
            }
        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        notificationBuilder = createNotificationBuilder().apply {
            updateNotificationWithState(closeToMe.state.value)
            applyNotificationResultCount(0)
        }.also {
            startForeground(Constants.NOTIFICATION_ID, it.build())
        }

        when (intent?.action) {
            BeaconServiceActions.ACTION_START_BEACON -> closeToMe.start()
            BeaconServiceActions.ACTION_STOP_BEACON -> closeToMe.stop()
        }

        // return START_NOT_STICKY
        return super.onStartCommand(intent, flags, startId)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            Constants.NOTIFICATION_CHANNEL_ID,
            Constants.NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = Constants.NOTIFICATION_CHANNEL_DESC
            enableLights(false)
            enableVibration(false)
            setSound(null, null)
        }
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(serviceChannel)
    }

    private fun createNotificationBuilder(): NotificationCompat.Builder {
        val pendingIntent = PendingIntent.getActivity(
            this,
            MAIN_ACTIVITY_REQUEST_CODE,
            MainActivity.intent(this),
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID).apply {
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            setContentIntent(pendingIntent)
            setShowWhen(false)
            setSound(null)
            setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_notification_off))
            setCategory(NotificationCompat.CATEGORY_STATUS)
            color = ResourcesCompat.getColor(resources, R.color.accent, theme)
            priority = NotificationCompat.PRIORITY_DEFAULT
        }
    }

    @SuppressLint("RestrictedApi")
    private fun NotificationCompat.Builder.updateNotificationWithState(state: CloseToMeState?): NotificationCompat.Builder {
        if (state == CloseToMeState.STARTED) {
            setSmallIcon(R.drawable.ic_notification_on)
            setContentTitle("CoviLights is running")

            mActions.clear()

            // addAction(
            //     NotificationCompat.Action.Builder(
            //         R.drawable.ic_notification_off,
            //         "Stop",
            //         PendingIntent.getService(
            //             this@BeaconService,
            //             MAIN_SERVICE_REQUEST_CODE,
            //             BeaconServiceActions.StopBeacon.toIntent(this@BeaconService),
            //             PendingIntent.FLAG_UPDATE_CURRENT
            //         )
            //     ).build()
            // )
        } else {
            setSmallIcon(R.drawable.ic_notification_off)
            setContentTitle("CoviLights stopped")

            mActions.clear()

            // addAction(
            //     NotificationCompat.Action.Builder(
            //         R.drawable.ic_notification_on,
            //         "Start",
            //         PendingIntent.getService(
            //             this@BeaconService,
            //             MAIN_SERVICE_REQUEST_CODE,
            //             BeaconServiceActions.StartBeacon.toIntent(this@BeaconService),
            //             PendingIntent.FLAG_UPDATE_CURRENT
            //         )
            //     ).build()
            // )
        }

        return this
    }

    private fun NotificationCompat.Builder.applyNotificationResultCount(resultCount: Int): NotificationCompat.Builder {
        if (resultCount > 0) {
            setContentText("People around: $resultCount")
        } else {
            setContentText("No people around")
        }
        return this
    }

    companion object {
        private const val MAIN_ACTIVITY_REQUEST_CODE = 2001
        // private const val MAIN_SERVICE_REQUEST_CODE = 2002
    }
}
