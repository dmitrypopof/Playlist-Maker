package com.example.playlistmaker.feature.media.presentation.favorite

sealed class FavoriteTracksEvent {
    data class NavigateToPlayer(val track: com.example.playlistmaker.feature.search.domain.model.Track) : FavoriteTracksEvent()
}