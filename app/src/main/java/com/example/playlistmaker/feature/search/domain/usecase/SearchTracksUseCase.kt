package com.example.playlistmaker.feature.search.domain.usecase

import com.example.playlistmaker.feature.search.domain.model.Track
import com.example.playlistmaker.feature.search.domain.repository.TrackRepository

class SearchTracksUseCase (private val repository: TrackRepository) {
    operator fun invoke(query: String): Result<List<Track>> {
        if (query.isBlank()) {
            return Result.failure(IllegalArgumentException("Query cannot be empty"))
        }
        return repository.searchTracks(query)
    }
}