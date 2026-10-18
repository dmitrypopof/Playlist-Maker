package com.example.playlistmaker.feature.search.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.core.utils.SingleLiveEvent
import com.example.playlistmaker.feature.search.domain.model.Track
import com.example.playlistmaker.feature.search.domain.usecase.AddTrackToHistoryUseCase
import com.example.playlistmaker.feature.search.domain.usecase.ClearSearchHistoryUseCase
import com.example.playlistmaker.feature.search.domain.usecase.GetSearchHistoryUseCase
import com.example.playlistmaker.feature.search.domain.usecase.SearchTracksUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchTracksUseCase: SearchTracksUseCase,
    private val getSearchHistoryUseCase: GetSearchHistoryUseCase,
    private val addTrackToHistoryUseCase: AddTrackToHistoryUseCase,
    private val clearSearchHistoryUseCase: ClearSearchHistoryUseCase
) : ViewModel() {

    companion object {
        private const val SEARCH_DELAY_MS = 2000L
    }
    private var debounceJob: Job? = null
    private var searchJob: Job? = null


    // Состояние экрана
    private val _state = MutableLiveData<SearchState>()
    val state: LiveData<SearchState> = _state


    // События для навигации
    private val _events = SingleLiveEvent<SearchEvent>()
    val events: LiveData<SearchEvent> = _events

    private var currentQuery: String = ""

    private var lastSearchResults: List<Track>? = null

    init {
        showHistoryOrEmpty()
    }

     //Обработка изменения текста в поле поиска
    fun onSearchTextChanged(query: String) {
        currentQuery = query

         debounceJob?.cancel()

        if (query.isEmpty()) {
            searchJob?.cancel()
            showHistoryOrEmpty()
        } else {
            debounceJob = viewModelScope.launch {
                delay(SEARCH_DELAY_MS)
                performSearch(query)
            }
        }
    }

    fun onSearchFocusChanged(hasFocus: Boolean) {
        if (hasFocus && currentQuery.isEmpty()) {
            showHistoryOrEmpty()
        }
    }

    private fun performSearch(query: String) {
        _state.value = SearchState.Loading

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            searchTracksUseCase(query).collect { result ->
                result.onSuccess { tracks ->
                    if (tracks.isEmpty()) {
                        _state.value = SearchState.Empty
                    } else {
                        lastSearchResults = tracks
                        _state.value = SearchState.Content(tracks)
                    }
                }.onFailure {
                    _state.value = SearchState.NetworkError
                }
            }
        }
    }

    // Показать историю или пустое состояние
    private fun showHistoryOrEmpty() {
        viewModelScope.launch {
            val history = getSearchHistoryUseCase()
            if (history.isNotEmpty()) {
                _state.value = SearchState.HistoryContent(history)
            } else {
                _state.value = SearchState.HistoryEmpty
            }
        }
    }

    //Обработка клика по треку
    fun onTrackClicked(track: Track) {
        addTrackToHistoryUseCase(track)
        _events.value = SearchEvent.NavigateToPlayer(track.trackId)
    }

    //Обновить историю (используется при возвращении на экран)
    fun refreshHistory() {
        if (currentQuery.isEmpty()) {
            showHistoryOrEmpty()
        }
    }

    //Восстановление результатов поиска при возвращении на экран
    fun restoreSearchResults() {
        if (currentQuery.isNotEmpty()) {
            if (lastSearchResults != null) {
                // Показываем сохраненные результаты
                lastSearchResults?.let { tracks ->
                    if (tracks.isEmpty()) {
                        _state.value = SearchState.Empty
                    } else {
                        _state.value = SearchState.Content(tracks)
                    }
                }
            } else {
                performSearch(currentQuery)
            }
        } else {
            showHistoryOrEmpty()
        }
    }

    //Очистка истории
    fun onClearHistoryClicked() {
        clearSearchHistoryUseCase()
        if (currentQuery.isEmpty()) {
            _state.value = SearchState.HistoryEmpty
        }
    }

    //Повторный поиск при ошибке сети
    fun onRetryClicked() {
        if (currentQuery.isNotEmpty()) {
            performSearch(currentQuery)
        }
    }

    //Очистка поискового запроса
    fun onClearQueryClicked() {
        currentQuery = ""
        lastSearchResults = null
        debounceJob?.cancel()
        searchJob?.cancel()
        showHistoryOrEmpty()
    }

    override fun onCleared() {
        super.onCleared()
        debounceJob?.cancel()
        searchJob?.cancel()
    }
    suspend fun getTrackById(trackId: Long): Track? {
        // Ищем трек в истории или в последних результатах поиска
        return getSearchHistoryUseCase().find { it.trackId == trackId }
            ?: lastSearchResults?.find { it.trackId == trackId }
    }
}