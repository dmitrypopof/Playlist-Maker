package com.example.playlistmaker.di

import com.example.playlistmaker.feature.favorite.data.converters.FavoriteTrackDbConvertor
import com.example.playlistmaker.feature.favorite.data.repository.FavoriteTracksRepositoryImpl
import com.example.playlistmaker.feature.favorite.domain.repository.FavoriteTracksRepository
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
        TrackRepositoryImpl(get(), get())
    }

    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(get(named("history_prefs")), get(),get())
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(get(named("app_settings")))
    }

    single<PlayerRepository> {
        PlayerRepositoryImpl()
    }

    factory { FavoriteTrackDbConvertor() }

    single<FavoriteTracksRepository> {
        FavoriteTracksRepositoryImpl(get(), get())
    }

}