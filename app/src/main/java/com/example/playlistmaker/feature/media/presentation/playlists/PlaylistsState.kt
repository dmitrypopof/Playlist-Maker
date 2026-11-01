package com.example.playlistmaker.feature.media.presentation.playlists

import com.example.playlistmaker.feature.media.domain.model.Playlist

sealed class PlaylistsState {
    object Empty : PlaylistsState()
    data class Content(val playlists: List<Playlist>) : PlaylistsState()
}