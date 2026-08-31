package com.example.playlistmaker.di

import com.example.playlistmaker.feature.player.data.repository.PlayerRepositoryImpl
import com.example.playlistmaker.feature.player.domain.repository.PlayerRepository
import com.example.playlistmaker.feature.search.data.repository.SearchHistoryRepositoryImpl
import com.example.playlistmaker.feature.search.data.repository.TrackRepositoryImpl
import com.example.playlistmaker.feature.search.domain.repository.SearchHistoryRepository
import com.example.playlistmaker.feature.search.domain.repository.TrackRepository
import com.example.playlistmaker.feature.settings.data.repository.SettingsRepositoryImpl
import com.example.playlistmaker.feature.settings.domain.repository.SettingsRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module

val repositoryModule = module {

    single<TrackRepository> {
        TrackRepositoryImpl(get())
    }

    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(get(named("history_prefs")), get())
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(get(named("app_settings")))
    }

    single<PlayerRepository> {
        PlayerRepositoryImpl()
    }
}