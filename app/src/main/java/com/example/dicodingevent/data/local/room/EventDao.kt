package com.example.dicodingevent.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.dicodingevent.data.local.entity.FavoriteEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(event: FavoriteEventEntity)

    @Query("DELETE FROM favorite_event WHERE id = :eventId")
    suspend fun deleteFavorite(eventId: Int)

    @Query("SELECT * FROM favorite_event")
    fun getAllFavoriteEvents(): Flow<List<FavoriteEventEntity>>


    @Query("SELECT id FROM favorite_event")
    suspend fun getAllFavoriteIds(): List<Int>


    @Query("SELECT EXISTS(SELECT 1 FROM favorite_event WHERE id = :eventId)")
    suspend fun isEventFavorite(eventId: Int): Boolean
}