package com.rfv.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Event::class], version = 1)
@TypeConverters(Converters::class)
abstract class RfvDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao

    companion object {
        fun build(context: Context): RfvDatabase = Room.databaseBuilder(
            context.applicationContext,
            RfvDatabase::class.java,
            "rfv.db"
        ).build()
    }
}
