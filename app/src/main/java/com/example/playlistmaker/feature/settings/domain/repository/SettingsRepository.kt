package com.example.playlistmaker.feature.settings.domain.repository

interface SettingsRepository {
    fun getThemeSettings(): Boolean
    fun updateThemeSettings(isDark: Boolean)
}