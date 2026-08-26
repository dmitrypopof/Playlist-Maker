package com.example.playlistmaker.feature.player.presentation

import com.example.playlistmaker.feature.search.domain.model.Track

sealed class AudioPlayerState {
    object Default : AudioPlayerState()
    object Prepared : AudioPlayerState()
    object Playing : AudioPlayerState()
    object Paused : AudioPlayerState()

    data class Content(
        val track: Track,
        val isPlaying: Boolean = false,
        val currentPosition: String = "00:00"
    ) : AudioPlayerState()

    data class Progress(
        val currentPosition: String
    ) : AudioPlayerState()
}