package com.example.playlistmaker.feature.search.domain.repository

import com.example.playlistmaker.feature.search.domain.model.Track

interface SearchHistoryRepository {
    fun getHistory(): List<Track>
    fun addTrack(track: Track)
    fun clearHistory()
}