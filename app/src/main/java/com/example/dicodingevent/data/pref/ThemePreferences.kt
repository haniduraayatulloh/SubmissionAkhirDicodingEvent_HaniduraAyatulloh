package com.example.dicodingevent.data.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class ThemePreferences private constructor(private val dataStore: DataStore<Preferences>) {

    private val THEME_KEY = intPreferencesKey("theme_setting")

    private val DEFAULT_THEME_MODE = -1


    fun getThemeSetting(): Flow<Int> {
        return dataStore.data.map { preferences ->
            preferences[THEME_KEY] ?: DEFAULT_THEME_MODE
        }
    }


    suspend fun saveThemeSetting(mode: Int) {
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = mode
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ThemePreferences? = null

        fun getInstance(context: Context): ThemePreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = INSTANCE ?: ThemePreferences(context.dataStore).also {
                    INSTANCE = it
                }
                instance
            }
        }
    }
}