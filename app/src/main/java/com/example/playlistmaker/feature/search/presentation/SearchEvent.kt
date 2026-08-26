package com.example.playlistmaker.feature.search.presentation

sealed class SearchEvent {
    data class NavigateToPlayer(val trackId: Long) : SearchEvent()
}