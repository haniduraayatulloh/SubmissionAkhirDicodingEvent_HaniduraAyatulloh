package com.example.dicodingevent.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dicodingevent.data.datastore.ThemePreferences
import com.example.dicodingevent.data.repository.EventRepository
import com.example.dicodingevent.ui.viewmodel.EventViewModel
import com.example.dicodingevent.ui.viewmodel.ThemeViewModel

class ViewModelFactory(
    private val repository: EventRepository,
    private val pref: ThemePreferences // Tipe data ini HARUS dari .../data/datastore/
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(EventViewModel::class.java) -> {
                EventViewModel(repository) as T
            }
            modelClass.isAssignableFrom(ThemeViewModel::class.java) -> {
                ThemeViewModel(pref) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory {
            return instance ?: synchronized(this) {
                instance ?: ViewModelFactory(
                    Injection.provideRepository(context),
                    Injection.providePreferences(context) // Ini akan mengembalikan .../data/datastore/ThemePreferences
                ).also { instance = it }
            }
        }
    }
}