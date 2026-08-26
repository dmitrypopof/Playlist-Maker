package com.example.playlistmaker.feature.search.presentation

import com.example.playlistmaker.feature.search.domain.model.Track

sealed class SearchEvent {
    data class NavigateToPlayer(val trackId: Long) : SearchEvent()
}