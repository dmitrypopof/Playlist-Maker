package com.example.playlistmaker.domain.usecase

import com.example.playlistmaker.domain.repository.SettingsRepository

class GetThemeSettingsUseCase(private val repository: SettingsRepository) {
    operator fun invoke(): Boolean {
        return repository.getThemeSettings()
    }
}