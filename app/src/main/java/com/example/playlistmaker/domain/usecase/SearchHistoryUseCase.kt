package com.example.playlistmaker.domain.usecase

import com.example.playlistmaker.domain.model.Track

class SearchHistoryUseCase {
    private val history = mutableListOf<Track>()
    private val maxSize = 10

    fun getHistory(): List<Track> {
        return history.toList()
    }

    fun addTrack(track: Track) {
        history.removeAll { it.trackName == track.trackName && it.artistName == track.artistName }
        history.add(0, track)
        if (history.size > maxSize) {
            history.removeAt(history.size - 1)
        }
    }

    fun clearHistory() {
        history.clear()
    }
}