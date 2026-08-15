package com.example.playlistmaker.feature.settings

import android.content.Context
import com.example.playlistmaker.domain.repository.SettingsRepository

class SettingsRepositoryImpl(context: Context) : SettingsRepository {
    private val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

    override fun getThemeSettings(): Boolean {
        return prefs.getBoolean("theme_enabled", false)
    }

    override fun updateThemeSettings(isDark: Boolean) {
        prefs.edit().putBoolean("theme_enabled", isDark).apply()
    }
}