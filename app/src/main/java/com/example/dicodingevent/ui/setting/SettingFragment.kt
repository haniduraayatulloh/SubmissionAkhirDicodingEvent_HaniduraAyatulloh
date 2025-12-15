package com.example.dicodingevent.ui.setting

import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.viewModels
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import com.example.dicodingevent.R
import com.example.dicodingevent.di.ViewModelFactory
import com.example.dicodingevent.ui.viewmodel.ThemeViewModel

class SettingFragment : PreferenceFragmentCompat() {

    private val viewModel: ThemeViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        val themePreference = findPreference<ListPreference>(getString(R.string.pref_key_theme))

        themePreference?.setOnPreferenceChangeListener { _, newValue ->
            val newMode = newValue.toString().toInt()
            viewModel.saveThemeSetting(newMode)
            AppCompatDelegate.setDefaultNightMode(newMode)
            true
        }
    }
}