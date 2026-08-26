package com.example.playlistmaker.feature.settings.presentation

sealed class SettingsState {
    data class ThemeSettings(
        val isDarkTheme: Boolean
    ) : SettingsState()
}