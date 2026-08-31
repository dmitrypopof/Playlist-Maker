package com.example.playlistmaker.feature.settings.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.feature.settings.domain.repository.SettingsRepository

class SettingsRepositoryImpl(private val prefs: SharedPreferences
) : SettingsRepository {

    override fun getThemeSettings(): Boolean {
        return prefs.getBoolean("theme_enabled", false)
    }

    override fun updateThemeSettings(isDark: Boolean) {
        prefs.edit().putBoolean("theme_enabled", isDark).apply()
    }
}