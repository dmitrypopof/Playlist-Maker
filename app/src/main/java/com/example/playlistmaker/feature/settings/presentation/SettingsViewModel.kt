package com.example.playlistmaker.feature.settings.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.feature.settings.domain.usecase.GetThemeSettingsUseCase
import com.example.playlistmaker.feature.settings.domain.usecase.UpdateThemeSettingsUseCase

class SettingsViewModel (
    private val getThemeSettingsUseCase: GetThemeSettingsUseCase,
    private val updateThemeSettingsUseCase: UpdateThemeSettingsUseCase
) : ViewModel() {

    // Состояние экрана
    private val _state = MutableLiveData<SettingsState>()
    val state: LiveData<SettingsState> = _state

    init {
        // Загружаем текущие настройки темы
        loadThemeSettings()
    }

    /**
     * Загрузка настроек темы
     */
    private fun loadThemeSettings() {
        val isDarkTheme = getThemeSettingsUseCase()
        _state.value = SettingsState.ThemeSettings(isDarkTheme)
    }

    /**
     * Обработка изменения темы
     */
    fun onThemeChanged(isDarkTheme: Boolean) {
        updateThemeSettingsUseCase(isDarkTheme)
        // Обновляем состояние
        _state.value = SettingsState.ThemeSettings(isDarkTheme)
    }

    /**
     * Получить текущее состояние темы
     */
    fun getCurrentTheme(): Boolean {
        return getThemeSettingsUseCase()
    }
}