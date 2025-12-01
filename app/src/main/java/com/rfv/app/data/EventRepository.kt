package com.rfv.app.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.time.Instant

class EventRepository(
    private val dao: EventDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    val events: Flow<List<Event>> = dao.observeEvents()

    suspend fun logEvent(
        type: String,
        title: String,
        start: Instant,
        end: Instant? = null,
        note: String? = null,
        latitude: Double? = null,
        longitude: Double? = null
    ) {
        withContext(dispatcher) {
            dao.upsert(
                Event(
                    type = type,
                    title = title,
                    start = start,
                    end = end,
                    note = note,
                    latitude = latitude,
                    longitude = longitude
                )
            )
        }
    }
}
