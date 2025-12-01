package com.rfv.app.data

import androidx.room.TypeConverter
import java.time.Instant

class Converters {
    @TypeConverter
    fun fromEpoch(value: Long?): Instant? = value?.let { Instant.ofEpochMilli(it) }

    @TypeConverter
    fun toEpoch(value: Instant?): Long? = value?.toEpochMilli()
}
