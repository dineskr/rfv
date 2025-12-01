package com.rfv.app.tracking

import android.app.Notification
import android.app.Service
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.rfv.app.R
import com.rfv.app.RfvApp
import com.rfv.app.data.EventRepository
import com.rfv.app.data.RfvDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.Instant

class ForegroundUsageService : Service() {
    private val scope = CoroutineScope(Dispatchers.IO)
    private var pollJob: Job? = null
    private var lastTimestamp: Long = 0L
    private lateinit var repo: EventRepository

    override fun onCreate() {
        super.onCreate()
        repo = EventRepository(RfvDatabase.build(this).eventDao())
        startForeground(1, buildNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        pollJob?.cancel()
        pollJob = scope.launch { pollUsageEvents() }
        return START_STICKY
    }

    override fun onDestroy() {
        pollJob?.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(this, RfvApp.CHANNEL_TRACKING)
            .setContentTitle("Screen time tracking")
            .setContentText("Monitoring foreground apps")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .build()
    }

    private suspend fun pollUsageEvents() {
        val usageStats = getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager
        lastTimestamp = System.currentTimeMillis()

        while (isActive) {
            val end = System.currentTimeMillis()
            val events: UsageEvents = usageStats.queryEvents(lastTimestamp, end)
            var startEvent: UsageEvents.Event? = null
            while (events.hasNextEvent()) {
                val event = UsageEvents.Event()
                events.getNextEvent(event)
                if (event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    startEvent = event
                }
                if (event.eventType == UsageEvents.Event.MOVE_TO_BACKGROUND && startEvent != null && startEvent.packageName == event.packageName) {
                    repo.logScreenTime(
                        appPackage = event.packageName,
                        start = Instant.ofEpochMilli(startEvent.timeStamp),
                        end = Instant.ofEpochMilli(event.timeStamp)
                    )
                    startEvent = null
                }
            }
            lastTimestamp = end
            delay(15000)
        }
    }
}
