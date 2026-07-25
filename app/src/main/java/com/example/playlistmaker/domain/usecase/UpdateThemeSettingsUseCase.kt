package com.example.playlistmaker.domain.usecase

import com.example.playlistmaker.domain.repository.SettingsRepository

class UpdateThemeSettingsUseCase (private val repository: SettingsRepository) {
    operator fun invoke(isDark: Boolean) {
        repository.updateThemeSettings(isDark)
    }
}