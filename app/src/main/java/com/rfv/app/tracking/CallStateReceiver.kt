package com.rfv.app.tracking

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import com.rfv.app.data.EventRepository
import com.rfv.app.data.RfvDatabase
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

class CallStateReceiver : BroadcastReceiver() {
    private val startTimes = ConcurrentHashMap<String, Instant>()

    override fun onReceive(context: Context, intent: Intent) {
        val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE) ?: return
        val number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
        val repo = EventRepository(RfvDatabase.build(context).eventDao())

        when (state) {
            TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                startTimes[number ?: "unknown"] = Instant.now()
            }
            TelephonyManager.EXTRA_STATE_IDLE -> {
                val key = number ?: "unknown"
                val start = startTimes.remove(key) ?: return
                repo.logCall(number, start, Instant.now())
            }
        }
    }
}
