package com.rfv.app.tracking

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.rfv.app.R
import com.rfv.app.RfvApp

class ForegroundUsageService : Service() {
    override fun onCreate() {
        super.onCreate()
        startForeground(NOTIFICATION_ID, buildNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Future implementation: track foreground app usage.
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun buildNotification(): Notification {
        val manager = getSystemService(NotificationManager::class.java)
        val channelId = RfvApp.CHANNEL_TRACKING

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Tracking screen time")
            .setContentText("Foreground usage monitor is running")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setSilent(true)
            .build()
            .also { manager.notify(NOTIFICATION_ID, it) }
    }

    companion object {
        private const val NOTIFICATION_ID = 1001
    }
}
