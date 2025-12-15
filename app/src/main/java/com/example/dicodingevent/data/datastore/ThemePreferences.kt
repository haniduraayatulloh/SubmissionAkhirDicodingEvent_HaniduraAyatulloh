package com.example.dicodingevent.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ThemePreferences private constructor(private val dataStore: DataStore<Preferences>) {

    private val THEME_KEY = intPreferencesKey("theme_setting")

    fun getThemeSetting(): Flow<Int> {
        return dataStore.data.map {
            it[THEME_KEY] ?: -1 // Default to system setting
        }
    }

    suspend fun saveThemeSetting(themeMode: Int) {
        dataStore.edit {
            it[THEME_KEY] = themeMode
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ThemePreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): ThemePreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = ThemePreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}