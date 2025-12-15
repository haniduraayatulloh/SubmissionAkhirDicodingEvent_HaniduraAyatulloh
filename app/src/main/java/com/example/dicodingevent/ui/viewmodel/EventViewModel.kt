package com.example.dicodingevent.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.dicodingevent.data.model.EventItem
import com.example.dicodingevent.data.repository.EventRepository
import kotlinx.coroutines.launch

sealed class EventResult<out T> {
    data class Success<T>(val data: T) : EventResult<T>()
    data class Error(val exception: Exception) : EventResult<Nothing>()
    object Loading : EventResult<Nothing>()
}

class EventViewModel(private val repository: EventRepository) : ViewModel() {

    private val _upcomingEvents = MutableLiveData<EventResult<List<EventItem>>>()
    val upcomingEvents: LiveData<EventResult<List<EventItem>>> = _upcomingEvents

    private val _finishedEvents = MutableLiveData<EventResult<List<EventItem>>>()
    val finishedEvents: LiveData<EventResult<List<EventItem>>> = _finishedEvents

    private val _detailEvent = MutableLiveData<EventResult<EventItem>>()
    val detailEvent: LiveData<EventResult<EventItem>> = _detailEvent

    private val _searchResults = MutableLiveData<EventResult<List<EventItem>>>()
    val searchResults: LiveData<EventResult<List<EventItem>>> = _searchResults

    val favoriteEvents: LiveData<List<EventItem>> = repository.getFavoriteEvents().asLiveData()

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> = _isFavorite

    init {
        fetchUpcomingEvents()
        fetchFinishedEvents()
    }

    fun fetchUpcomingEvents() {
        viewModelScope.launch {
            _upcomingEvents.value = EventResult.Loading
            try {
                _upcomingEvents.value = EventResult.Success(repository.getUpcomingEvents())
            } catch (e: Exception) {
                _upcomingEvents.value = EventResult.Error(e)
            }
        }
    }

    fun fetchFinishedEvents() {
        viewModelScope.launch {
            _finishedEvents.value = EventResult.Loading
            try {
                _finishedEvents.value = EventResult.Success(repository.getFinishedEvents())
            } catch (e: Exception) {
                _finishedEvents.value = EventResult.Error(e)
            }
        }
    }

    fun fetchDetailEvent(eventId: Int) {
        viewModelScope.launch {
            _detailEvent.value = EventResult.Loading
            try {
                val eventDetail = repository.getEventDetail(eventId)
                if (eventDetail != null) {
                    _detailEvent.value = EventResult.Success(eventDetail)
                    _isFavorite.value = eventDetail.isFavorite
                } else {
                    _detailEvent.value = EventResult.Error(Exception("Event not found"))
                }
            } catch (e: Exception) {
                _detailEvent.value = EventResult.Error(e)
            }
        }
    }

    fun searchEvents(query: String) {
        viewModelScope.launch {
            _searchResults.value = EventResult.Loading
            try {
                _searchResults.value = EventResult.Success(repository.searchEvents(query))
            } catch (e: Exception) {
                _searchResults.value = EventResult.Error(e)
            }
        }
    }

    fun toggleFavorite(event: EventItem) {
        viewModelScope.launch {
            val isCurrentlyFavorite = repository.isEventFavorite(event.id)
            if (isCurrentlyFavorite) {
                repository.deleteFavorite(event.id)
            } else {
                repository.saveFavorite(event)
            }
            _isFavorite.value = !isCurrentlyFavorite

            updateItemInList(_upcomingEvents, event.id, !isCurrentlyFavorite)
            updateItemInList(_finishedEvents, event.id, !isCurrentlyFavorite)
            updateItemInList(_searchResults, event.id, !isCurrentlyFavorite)
        }
    }

    private fun updateItemInList(liveData: MutableLiveData<EventResult<List<EventItem>>>, eventId: Int, newFavStatus: Boolean) {
        val currentResult = liveData.value
        if (currentResult is EventResult.Success) {
            val updatedList = currentResult.data.map {
                if (it.id == eventId) it.copy(isFavorite = newFavStatus) else it
            }
            liveData.value = EventResult.Success(updatedList)
        }
    }
}