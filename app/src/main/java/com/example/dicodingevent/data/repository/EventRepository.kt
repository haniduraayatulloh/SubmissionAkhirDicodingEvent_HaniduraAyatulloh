package com.example.dicodingevent.data.repository

import android.util.Log
import com.example.dicodingevent.data.local.room.EventDao
import com.example.dicodingevent.data.model.*
import com.example.dicodingevent.network.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class EventRepository private constructor(
    private val apiService: ApiService,
    private val eventDao: EventDao
) {

    private suspend fun fetchAndSyncEvents(fetchFromApi: suspend () -> List<EventItem>): List<EventItem> {
        return try {
            val apiEvents = fetchFromApi()
            val favoriteIds = eventDao.getAllFavoriteIds()
            apiEvents.map { event ->
                event.copy(isFavorite = favoriteIds.contains(event.id))
            }
        } catch (e: Exception) {
            Log.e("Repository", "Error fetching or syncing events: ${e.message}")
            emptyList()
        }
    }

    suspend fun getUpcomingEvents(): List<EventItem> {
        return fetchAndSyncEvents { apiService.getUpcomingEvents().listEvents?.filterNotNull() ?: emptyList() }
    }

    suspend fun getFinishedEvents(): List<EventItem> {
        return fetchAndSyncEvents { apiService.getFinishedEvents().listEvents?.filterNotNull() ?: emptyList() }
    }

    suspend fun getEventDetail(id: Int): EventItem? {
        return try {
            val response = apiService.getEventDetailResponse(id)
            if (!response.error) {
                val isFav = eventDao.isEventFavorite(id)
                response.event?.copy(isFavorite = isFav)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun searchEvents(query: String): List<EventItem> {
        val allEvents = getUpcomingEvents() + getFinishedEvents()
        return allEvents.filter {
            it.name.contains(query, ignoreCase = true) ||
                    it.ownerName?.contains(query, ignoreCase = true) == true
        }.distinctBy { it.id }
    }

    fun getFavoriteEvents(): Flow<List<EventItem>> {
        return eventDao.getAllFavoriteEvents().map { entities ->
            entities.map { it.toEventItem() }
        }
    }

    suspend fun isEventFavorite(eventId: Int): Boolean {
        return eventDao.isEventFavorite(eventId)
    }

    suspend fun saveFavorite(event: EventItem) {
        eventDao.insertFavorite(event.toFavoriteEntity())
    }

    suspend fun deleteFavorite(eventId: Int) {
        eventDao.deleteFavorite(eventId)
    }

    companion object {
        @Volatile
        private var instance: EventRepository? = null

        fun getInstance(apiService: ApiService, eventDao: EventDao): EventRepository =
            instance ?: synchronized(this) {
                instance ?: EventRepository(apiService, eventDao).also { instance = it }
            }
    }
}