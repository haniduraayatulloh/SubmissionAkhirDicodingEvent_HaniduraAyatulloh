package com.example.dicodingevent.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.dicodingevent.data.local.entity.FavoriteEventEntity

@Database(entities = [FavoriteEventEntity::class], version = 3, exportSchema = false) // VERSI DITINGKATKAN
abstract class EventDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao

    companion object {
        @Volatile
        private var instance: EventDatabase? = null

        fun getInstance(context: Context): EventDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    EventDatabase::class.java, "Event.db"
                )
                    .fallbackToDestructiveMigration()
                    .build().also { instance = it }
            }
    }
}