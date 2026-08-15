package com.example.playlistmaker.feature.search.domain.usecase

import com.example.playlistmaker.feature.search.domain.model.Track
import com.example.playlistmaker.feature.search.domain.repository.SearchHistoryRepository

class GetSearchHistoryUseCase(
    private val repository: SearchHistoryRepository
) {
    operator fun invoke(): List<Track> {
        return repository.getHistory()
    }
}