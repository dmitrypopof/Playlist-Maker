package com.example.playlistmaker.feature.media.presentation.favorite

import com.example.playlistmaker.feature.search.domain.model.Track

sealed class FavoriteTracksState {

    data class Content(
        val tracks: List<Track>
    ) : FavoriteTracksState()

    object Empty : FavoriteTracksState()
}