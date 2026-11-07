package com.example.playlistmaker.feature.media.presentation.playlist

import com.example.playlistmaker.feature.media.domain.model.Playlist
import com.example.playlistmaker.feature.search.domain.model.Track

sealed class PlaylistState {

    object Loading : PlaylistState()

    data class Content(
        val playlist: Playlist,
        val tracks: List<Track>,
        val totalDurationMinutes: String
    ) : PlaylistState()
}