package com.example.playlistmaker.feature.media.presentation.newplaylist

sealed class NewPlaylistState {
    object Success : NewPlaylistState()
    data class Error(val message: String) : NewPlaylistState()
}