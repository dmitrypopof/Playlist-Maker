package com.example.playlistmaker.feature.player.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.feature.player.domain.repository.PlayerRepository

class AudioPlayerViewModelFactory (
    private val playerRepository: PlayerRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AudioPlayerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AudioPlayerViewModel(playerRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}