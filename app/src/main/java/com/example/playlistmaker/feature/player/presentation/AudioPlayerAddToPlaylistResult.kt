package com.example.playlistmaker.feature.player.presentation

sealed class AudioPlayerAddToPlaylistResult {
    data class Added(val playlistName: String) : AudioPlayerAddToPlaylistResult()
    data class AlreadyAdded(val playlistName: String) : AudioPlayerAddToPlaylistResult()
}