package com.example.playlistmaker.di

import com.example.playlistmaker.feature.search.domain.usecase.AddTrackToHistoryUseCase
import com.example.playlistmaker.feature.search.domain.usecase.ClearSearchHistoryUseCase
import com.example.playlistmaker.feature.search.domain.usecase.GetSearchHistoryUseCase
import com.example.playlistmaker.feature.search.domain.usecase.SearchTracksUseCase
import com.example.playlistmaker.feature.settings.domain.usecase.GetThemeSettingsUseCase
import com.example.playlistmaker.feature.settings.domain.usecase.UpdateThemeSettingsUseCase
import org.koin.dsl.module

val interactorModule = module {

    factory {
        SearchTracksUseCase(get())
    }

    factory {
        GetSearchHistoryUseCase(get())
    }

    factory {
        AddTrackToHistoryUseCase(get())
    }

    factory {
        ClearSearchHistoryUseCase(get())
    }

    factory {
        GetThemeSettingsUseCase(get())
    }

    factory {
        UpdateThemeSettingsUseCase(get())
    }
}