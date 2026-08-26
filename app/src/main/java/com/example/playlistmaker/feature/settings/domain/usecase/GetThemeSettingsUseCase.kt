package com.example.playlistmaker.feature.settings.domain.usecase

import com.example.playlistmaker.feature.settings.domain.repository.SettingsRepository

class GetThemeSettingsUseCase(private val repository: SettingsRepository) {
    operator fun invoke(): Boolean {
        return repository.getThemeSettings()
    }
}