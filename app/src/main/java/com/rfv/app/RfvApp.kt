package com.rfv.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class RfvApp : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val trackingChannel = NotificationChannel(
                CHANNEL_TRACKING,
                "RFV Tracking",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Ongoing timers and background tracking"
            }

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(trackingChannel)
        }
    }

    companion object {
        const val CHANNEL_TRACKING = "rfv_tracking"
    }
}
