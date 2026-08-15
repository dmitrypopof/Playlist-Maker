package com.example.playlistmaker.feature.settings.domain.usecase

import com.example.playlistmaker.feature.settings.domain.repository.SettingsRepository

class UpdateThemeSettingsUseCase (private val repository: SettingsRepository) {
    operator fun invoke(isDark: Boolean) {
        repository.updateThemeSettings(isDark)
    }
}