package com.covilights.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.Observer
import com.covilights.Constants
import com.covilights.DebugActivity
import com.covilights.R
import com.covilights.beacon.BeaconCallback
import com.covilights.beacon.BeaconManager
import com.covilights.beacon.BeaconState
import org.koin.android.ext.android.inject
import timber.log.Timber

class BeaconService : LifecycleService() {

    private val beaconManager: BeaconManager by inject()

    private var notificationBuilder: NotificationCompat.Builder? = null

    override fun onBind(intent: Intent): IBinder? {
        return super.onBind(intent)
    }

    override fun onCreate() {
        super.onCreate()

        beaconManager.state.observe(this, Observer { state ->
            notificationBuilder?.updateNotificationBeaconState(state)?.also {
                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(Constants.NOTIFICATION_ID, it.build())

                if (state != BeaconState.STARTED) ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_DETACH)
            }
        })

        beaconManager.results.observe(this, Observer { result ->
            val resultCount =
                result.values.filter { it.isVisible && it.isNear }.size
            notificationBuilder?.applyNotificationResultCount(resultCount)?.also {
                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(Constants.NOTIFICATION_ID, it.build())
            }
        })

        beaconManager.start(object : BeaconCallback {
            override fun onSuccess() {
                Timber.i("Beacon Manager started successfully.")
            }

            override fun onError(throwable: Throwable) {
                Timber.w(throwable, "Beacon Manager didn't start.")
            }
        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        notificationBuilder = createNotificationBuilder().apply {
            updateNotificationBeaconState(beaconManager.state.value)
            applyNotificationResultCount(0)
        }.also {
            startForeground(Constants.NOTIFICATION_ID, it.build())
        }

        when (intent?.action) {
            BeaconServiceActions.ACTION_START_BEACON -> beaconManager.start()
            BeaconServiceActions.ACTION_STOP_BEACON -> beaconManager.stop()
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
            DebugActivity.intent(this),
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
    private fun NotificationCompat.Builder.updateNotificationBeaconState(state: BeaconState?): NotificationCompat.Builder {
        when (state) {
            BeaconState.STARTED -> {
                setSmallIcon(R.drawable.ic_notification_on)
                setContentTitle("CoviLights Beacon Running")

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
            }
            else -> {
                setSmallIcon(R.drawable.ic_notification_off)
                setContentTitle("CoviLights Beacon Stopped")

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
        }

        return this
    }

    private fun NotificationCompat.Builder.applyNotificationResultCount(resultCount: Int): NotificationCompat.Builder {
        if (resultCount > 0) {
            setContentText("People around: $resultCount")
        } else {
            setContentText("No People around")
        }
        return this
    }

    companion object {
        private const val MAIN_ACTIVITY_REQUEST_CODE = 2001
        private const val MAIN_SERVICE_REQUEST_CODE = 2002
    }
}
