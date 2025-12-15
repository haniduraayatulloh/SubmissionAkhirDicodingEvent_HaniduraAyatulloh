package com.example.dicodingevent.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.dicodingevent.R
import com.example.dicodingevent.databinding.ActivityMainBinding
import com.example.dicodingevent.di.ViewModelFactory
import com.example.dicodingevent.ui.favorite.FavoriteFragment
import com.example.dicodingevent.ui.finished.FinishedEventFragment
import com.example.dicodingevent.ui.home.HomeFragment
import com.example.dicodingevent.ui.setting.SettingFragment
import com.example.dicodingevent.ui.upcoming.UpcomingEventFragment
import com.example.dicodingevent.ui.viewmodel.ThemeViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val themeViewModel: ThemeViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        themeViewModel.getThemeSettings().observe(this) { mode ->
            if (AppCompatDelegate.getDefaultNightMode() != mode) {
                AppCompatDelegate.setDefaultNightMode(mode)
            }
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> replaceFragment(HomeFragment())
                R.id.nav_upcoming -> replaceFragment(UpcomingEventFragment())
                R.id.nav_finished -> replaceFragment(FinishedEventFragment())
                R.id.nav_favorite -> replaceFragment(FavoriteFragment())
                R.id.nav_setting -> replaceFragment(SettingFragment())
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}