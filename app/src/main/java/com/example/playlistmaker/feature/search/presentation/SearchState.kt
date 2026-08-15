package com.example.playlistmaker.feature.search.presentation

import com.example.playlistmaker.feature.search.domain.model.Track

sealed class SearchState {
    object Loading : SearchState()
    object Empty : SearchState()
    object HistoryEmpty : SearchState()
    object NetworkError : SearchState()

    data class Content(
        val tracks: List<Track>
    ) : SearchState()

    data class HistoryContent(
        val tracks: List<Track>
    ) : SearchState()

    // Новое состояние - переход к плееру
    object NavigateToPlayer : SearchState()
}