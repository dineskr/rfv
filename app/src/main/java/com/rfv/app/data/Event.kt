package com.rfv.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "events")
data class Event(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String,
    val title: String,
    val start: Instant,
    val end: Instant?,
    val note: String?,
    val latitude: Double?,
    val longitude: Double?
)
