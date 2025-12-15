package com.example.dicodingevent.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.dicodingevent.data.datastore.ThemePreferences
import kotlinx.coroutines.launch

class ThemeViewModel(private val pref: ThemePreferences) : ViewModel() {

    fun getThemeSettings(): LiveData<Int> {
        return pref.getThemeSetting().asLiveData()
    }

    fun saveThemeSetting(themeMode: Int) {
        viewModelScope.launch {
            pref.saveThemeSetting(themeMode)
        }
    }
}
