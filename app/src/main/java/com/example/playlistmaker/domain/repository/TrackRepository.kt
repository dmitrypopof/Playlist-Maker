package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.model.Track

interface TrackRepository {
    fun searchTracks(query: String): Result<List<Track>>
}