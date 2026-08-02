package com.example.playlistmaker

import android.content.Context
import com.example.playlistmaker.data.repository.SettingsRepositoryImpl
import com.example.playlistmaker.data.repository.TrackRepositoryImpl
import com.example.playlistmaker.domain.repository.SettingsRepository
import com.example.playlistmaker.domain.repository.TrackRepository
import com.example.playlistmaker.domain.usecase.GetThemeSettingsUseCase
import com.example.playlistmaker.domain.usecase.SearchHistoryUseCase
import com.example.playlistmaker.domain.usecase.SearchTracksUseCase
import com.example.playlistmaker.domain.usecase.UpdateThemeSettingsUseCase

object Creator{
    private lateinit var applicationContext: Context

    fun init(context: Context) {
        applicationContext = context.applicationContext
    }

    private val trackRepository: TrackRepository by lazy {
        TrackRepositoryImpl()
    }

    private val settingsRepository: SettingsRepository by lazy {
        SettingsRepositoryImpl(applicationContext)
    }

    fun provideSearchTracksUseCase(): SearchTracksUseCase {
        return SearchTracksUseCase(trackRepository)
    }

    fun provideGetThemeSettingsUseCase(): GetThemeSettingsUseCase {
        return GetThemeSettingsUseCase(settingsRepository)
    }

    fun provideUpdateThemeSettingsUseCase(): UpdateThemeSettingsUseCase {
        return UpdateThemeSettingsUseCase(settingsRepository)
    }

    fun provideSearchHistoryUseCase(): SearchHistoryUseCase {
        return SearchHistoryUseCase(applicationContext)
    }
}