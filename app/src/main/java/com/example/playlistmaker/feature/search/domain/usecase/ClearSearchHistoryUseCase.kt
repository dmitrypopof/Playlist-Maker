package com.example.playlistmaker.feature.search.domain.usecase

import com.example.playlistmaker.feature.search.domain.repository.SearchHistoryRepository

class ClearSearchHistoryUseCase(
    private val repository: SearchHistoryRepository
) {
    operator fun invoke() {
        repository.clearHistory()
    }
}