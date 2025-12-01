package com.rfv.app.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant

class EventRepository(private val dao: EventDao) {
    private val scope = CoroutineScope(Dispatchers.IO)

    fun logCall(number: String?, start: Instant, end: Instant?) {
        scope.launch {
            val event = Event(
                type = EventType.CALL,
                title = number?.let { "Call with $it" },
                notes = null,
                start = start,
                end = end,
                appPackage = null,
                phoneNumber = number,
                locationLat = null,
                locationLng = null,
                locationName = null,
                calendarEventId = null,
                syncState = SyncState.PENDING
            )
            dao.upsert(event)
        }
    }

    fun logScreenTime(appPackage: String, start: Instant, end: Instant?) {
        scope.launch {
            val event = Event(
                type = EventType.SCREEN_TIME,
                title = appPackage,
                notes = null,
                start = start,
                end = end,
                appPackage = appPackage,
                phoneNumber = null,
                locationLat = null,
                locationLng = null,
                locationName = null,
                calendarEventId = null,
                syncState = SyncState.PENDING
            )
            dao.upsert(event)
        }
    }

    fun logManual(
        title: String?,
        notes: String?,
        start: Instant,
        end: Instant?,
        locationLat: Double?,
        locationLng: Double?,
        locationName: String?
    ) {
        scope.launch {
            val event = Event(
                type = EventType.MANUAL,
                title = title,
                notes = notes,
                start = start,
                end = end,
                appPackage = null,
                phoneNumber = null,
                locationLat = locationLat,
                locationLng = locationLng,
                locationName = locationName,
                calendarEventId = null,
                syncState = SyncState.PENDING
            )
            dao.upsert(event)
        }
    }
}
