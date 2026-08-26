package com.example.playlistmaker.feature.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.feature.settings.domain.usecase.GetThemeSettingsUseCase
import com.example.playlistmaker.feature.settings.domain.usecase.UpdateThemeSettingsUseCase

class SettingsViewModelFactory (
    private val getThemeSettingsUseCase: GetThemeSettingsUseCase,
    private val updateThemeSettingsUseCase: UpdateThemeSettingsUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(
                getThemeSettingsUseCase,
                updateThemeSettingsUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}