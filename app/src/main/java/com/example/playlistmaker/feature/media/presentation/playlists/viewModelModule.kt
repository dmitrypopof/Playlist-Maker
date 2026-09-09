package com.example.playlistmaker.feature.media.presentation.playlists

import com.example.playlistmaker.feature.player.presentation.AudioPlayerViewModel
import com.example.playlistmaker.feature.search.presentation.SearchViewModel
import com.example.playlistmaker.feature.settings.presentation.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        SearchViewModel(get(), get(), get(), get())
    }

    viewModel {
        AudioPlayerViewModel(get())
    }

    viewModel {
        SettingsViewModel(get(), get())
    }

    viewModel {
        PlaylistsViewModel()
    }
}