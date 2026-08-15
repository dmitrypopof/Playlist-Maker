package com.example.playlistmaker.feature.search.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.domain.usecase.SearchTracksUseCase
import com.example.playlistmaker.feature.search.domain.usecase.AddTrackToHistoryUseCase
import com.example.playlistmaker.feature.search.domain.usecase.ClearSearchHistoryUseCase
import com.example.playlistmaker.feature.search.domain.usecase.GetSearchHistoryUseCase

class SearchViewModelFactory (
    private val searchTracksUseCase: SearchTracksUseCase,
    private val getSearchHistoryUseCase: GetSearchHistoryUseCase,
    private val addTrackToHistoryUseCase: AddTrackToHistoryUseCase,
    private val clearSearchHistoryUseCase: ClearSearchHistoryUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SearchViewModel(
                searchTracksUseCase,
                getSearchHistoryUseCase,
                addTrackToHistoryUseCase,
                clearSearchHistoryUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}