package com.example.dicodingevent.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.dicodingevent.data.datastore.ThemePreferences
import com.example.dicodingevent.data.local.room.EventDatabase
import com.example.dicodingevent.data.repository.EventRepository
import com.example.dicodingevent.network.ApiConfig

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

object Injection {
    fun provideRepository(context: Context): EventRepository {
        val apiService = ApiConfig.getApiService()
        val database = EventDatabase.getInstance(context)
        val dao = database.eventDao()
        return EventRepository.getInstance(apiService, dao)
    }

    fun providePreferences(context: Context): ThemePreferences {
        return ThemePreferences.getInstance(context.dataStore)
    }
}