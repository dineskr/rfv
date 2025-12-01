package com.rfv.app.data

import androidx.room.TypeConverter
import java.time.Instant

class Converters {
    @TypeConverter
    fun fromInstant(value: Instant?): Long? = value?.toEpochMilli()

    @TypeConverter
    fun toInstant(value: Long?): Instant? = value?.let { Instant.ofEpochMilli(it) }

    @TypeConverter
    fun fromEventType(value: EventType?): String? = value?.name

    @TypeConverter
    fun toEventType(value: String?): EventType? = value?.let { EventType.valueOf(it) }

    @TypeConverter
    fun fromSyncState(value: SyncState?): String? = value?.name

    @TypeConverter
    fun toSyncState(value: String?): SyncState? = value?.let { SyncState.valueOf(it) }
}
