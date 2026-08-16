package com.example.playlistmaker.feature.search.presentation

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.feature.search.domain.usecase.SearchTracksUseCase
import com.example.playlistmaker.feature.search.domain.model.Track
import com.example.playlistmaker.feature.search.domain.usecase.AddTrackToHistoryUseCase
import com.example.playlistmaker.feature.search.domain.usecase.ClearSearchHistoryUseCase
import com.example.playlistmaker.feature.search.domain.usecase.GetSearchHistoryUseCase

class SearchViewModel(
    private val searchTracksUseCase: SearchTracksUseCase,
    private val getSearchHistoryUseCase: GetSearchHistoryUseCase,
    private val addTrackToHistoryUseCase: AddTrackToHistoryUseCase,
    private val clearSearchHistoryUseCase: ClearSearchHistoryUseCase
) : ViewModel() {

    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null
    private val SEARCH_DELAY_MS = 2000L

    // Состояние экрана
    private val _state = MutableLiveData<SearchState>()
    val state: LiveData<SearchState> = _state


    // События для навигации
    private val _events = MutableLiveData<SearchEvent>()
    val events: LiveData<SearchEvent> = _events

    private var currentQuery: String = ""

    private var lastSearchResults: List<Track>? = null

    init {
        showHistoryOrEmpty()
    }

     //Обработка изменения текста в поле поиска
    fun onSearchTextChanged(query: String) {
        currentQuery = query

        searchRunnable?.let { handler.removeCallbacks(it) }

        if (query.isEmpty()) {
            showHistoryOrEmpty()
        } else {
            searchRunnable = Runnable {
                performSearch(query)
            }
            searchRunnable?.let {
                handler.postDelayed(it, SEARCH_DELAY_MS)
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

        Thread {
            val result = searchTracksUseCase(query)

            handler.post {
                result.onSuccess { tracks ->
                    if (tracks.isEmpty()) {
                        _state.value = SearchState.Empty
                    } else {
                        _state.value = SearchState.Content(tracks)
                    }
                }.onFailure {
                    _state.value = SearchState.NetworkError
                }
            }
        }.start()
    }

    // Показать историю или пустое состояние
    private fun showHistoryOrEmpty() {
        val history = getSearchHistoryUseCase()
        if (history.isNotEmpty()) {
            _state.value = SearchState.HistoryContent(history)
        } else {
            _state.value = SearchState.HistoryEmpty
        }
    }

    //Обработка клика по треку
    fun onTrackClicked(track: Track) {
        // Добавляем в историю (фоново)
        addTrackToHistoryUseCase(track)
        // Отправляем событие навигации
        _events.value = SearchEvent.NavigateToPlayer(track)
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
        showHistoryOrEmpty()
    }

    override fun onCleared() {
        super.onCleared()
        searchRunnable?.let { handler.removeCallbacks(it) }
    }
}