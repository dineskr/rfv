package com.rfv.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.time.Instant

@Dao
interface EventDao {
    @Query("SELECT * FROM events WHERE type = :type ORDER BY start DESC")
    fun eventsByType(type: EventType): Flow<List<Event>>

    @Query("SELECT * FROM events WHERE start >= :start AND start <= :end ORDER BY start DESC")
    fun eventsBetween(start: Instant, end: Instant): Flow<List<Event>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(event: Event): Long

    @Update
    suspend fun update(event: Event)

    @Query("UPDATE events SET syncState = :state WHERE id = :id")
    suspend fun updateSyncState(id: Long, state: SyncState)
}
