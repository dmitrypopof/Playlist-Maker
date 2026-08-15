package com.example.playlistmaker.feature.search.data.mapper

import com.example.playlistmaker.core.network.dto.TrackDto
import com.example.playlistmaker.feature.search.domain.model.Track

object TrackMapper {
    fun map(dto: TrackDto): Track {
        return Track(
            trackName = dto.trackName ?: "Unknown Track",
            artistName = dto.artistName ?: "Unknown Artist",
            trackTime = dto.trackTime ?: 0,
            artworkUrl100 = dto.artworkUrl100 ?: "",
            collectionName = dto.collectionName ?: "Unknown Album",
            releaseDate = dto.releaseDate ?: "Unknown",
            primaryGenreName = dto.primaryGenreName ?: "Unknown",
            country = dto.country ?: "Unknown",
            previewUrl = dto.previewUrl
        )
    }
}