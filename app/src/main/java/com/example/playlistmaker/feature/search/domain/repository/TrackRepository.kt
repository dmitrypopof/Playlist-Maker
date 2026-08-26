package com.example.playlistmaker.feature.search.domain.repository

import com.example.playlistmaker.feature.search.domain.model.Track

interface TrackRepository {
    fun searchTracks(query: String): Result<List<Track>>
}