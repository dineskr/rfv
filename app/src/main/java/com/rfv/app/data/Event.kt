package com.rfv.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

enum class EventType { CALL, SCREEN_TIME, MANUAL }

enum class SyncState { PENDING, IN_PROGRESS, SYNCED, ERROR }

@Entity(tableName = "events")
data class Event(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: EventType,
    val title: String?,
    val notes: String?,
    val start: Instant,
    val end: Instant?,
    val appPackage: String?,
    val phoneNumber: String?,
    val locationLat: Double?,
    val locationLng: Double?,
    val locationName: String?,
    val calendarEventId: Long?,
    val syncState: SyncState,
    val updatedAt: Instant = Instant.now()
)
