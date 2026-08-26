package com.example.playlistmaker

import android.content.Context
import com.example.playlistmaker.feature.player.domain.repository.PlayerRepository
import com.example.playlistmaker.feature.player.data.repository.PlayerRepositoryImpl
import com.example.playlistmaker.feature.settings.data.repository.SettingsRepositoryImpl
import com.example.playlistmaker.feature.settings.domain.repository.SettingsRepository
import com.example.playlistmaker.feature.settings.domain.usecase.GetThemeSettingsUseCase
import com.example.playlistmaker.feature.search.domain.usecase.SearchTracksUseCase
import com.example.playlistmaker.feature.settings.domain.usecase.UpdateThemeSettingsUseCase
import com.example.playlistmaker.feature.search.data.repository.SearchHistoryRepositoryImpl
import com.example.playlistmaker.feature.search.data.repository.TrackRepositoryImpl
import com.example.playlistmaker.feature.search.domain.repository.SearchHistoryRepository
import com.example.playlistmaker.feature.search.domain.repository.TrackRepository
import com.example.playlistmaker.feature.search.domain.usecase.AddTrackToHistoryUseCase
import com.example.playlistmaker.feature.search.domain.usecase.ClearSearchHistoryUseCase
import com.example.playlistmaker.feature.search.domain.usecase.GetSearchHistoryUseCase
import com.google.gson.Gson

object Creator {
    private lateinit var applicationContext: Context
    private val gson = Gson()

    fun init(context: Context) {
        applicationContext = context.applicationContext
    }

    // Репозитории
    private val trackRepository: TrackRepository by lazy {
        TrackRepositoryImpl()
    }

    private val searchHistoryRepository: SearchHistoryRepository by lazy {
        SearchHistoryRepositoryImpl(
            prefs = applicationContext.getSharedPreferences(
                "history_prefs",
                Context.MODE_PRIVATE
            ),
            gson = gson
        )
    }

    private val settingsRepository: SettingsRepository by lazy {
        SettingsRepositoryImpl(applicationContext)
    }

    private val playerRepository: PlayerRepository by lazy {
        PlayerRepositoryImpl()
    }

    fun providePlayerRepository(): PlayerRepository {
        return playerRepository
    }

    // UseCase для поиска
    fun provideSearchTracksUseCase(): SearchTracksUseCase {
        return SearchTracksUseCase(trackRepository)
    }

    // UseCase для истории
    fun provideGetSearchHistoryUseCase(): GetSearchHistoryUseCase {
        return GetSearchHistoryUseCase(searchHistoryRepository)
    }

    fun provideAddTrackToHistoryUseCase(): AddTrackToHistoryUseCase {
        return AddTrackToHistoryUseCase(searchHistoryRepository)
    }

    fun provideClearSearchHistoryUseCase(): ClearSearchHistoryUseCase {
        return ClearSearchHistoryUseCase(searchHistoryRepository)
    }

    // UseCase для настроек
    fun provideGetThemeSettingsUseCase(): GetThemeSettingsUseCase {
        return GetThemeSettingsUseCase(settingsRepository)
    }

    fun provideUpdateThemeSettingsUseCase(): UpdateThemeSettingsUseCase {
        return UpdateThemeSettingsUseCase(settingsRepository)
    }
}