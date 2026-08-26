package com.example.playlistmaker.feature.search.domain.usecase

import com.example.playlistmaker.feature.search.domain.model.Track
import com.example.playlistmaker.feature.search.domain.repository.SearchHistoryRepository

class AddTrackToHistoryUseCase(
    private val repository: SearchHistoryRepository
) {
    operator fun invoke(track: Track) {
        repository.addTrack(track)
    }
}