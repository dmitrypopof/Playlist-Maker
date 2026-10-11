package com.example.playlistmaker.feature.search.domain.usecase

import com.example.playlistmaker.feature.search.domain.model.Track
import com.example.playlistmaker.feature.search.domain.repository.TrackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchTracksUseCase (private val repository: TrackRepository) {

    operator fun invoke(query: String): Flow<Result<List<Track>>> = flow {
        if (query.isBlank()) {
            emit(Result.failure(IllegalArgumentException("Query cannot be empty")))
        } else {
            repository.searchTracks(query).collect { result ->
                emit(result)
            }
        }
    }

}