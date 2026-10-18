package com.example.playlistmaker.feature.media.presentation.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.feature.favorite.domain.usecase.FavoriteTracksInteractor
import kotlinx.coroutines.launch

class FavoriteTracksViewModel(
    private val favoriteTracksInteractor: FavoriteTracksInteractor
) : ViewModel() {

    private val _state = MutableLiveData<FavoriteTracksState>()
    val state: LiveData<FavoriteTracksState> = _state

    init {
        //подписка на Flow избранных треков через viewModelScope
        viewModelScope.launch {
            favoriteTracksInteractor.getFavoriteTracks().collect { tracks ->
                _state.value = if (tracks.isEmpty()) {
                    FavoriteTracksState.Empty
                } else {
                    FavoriteTracksState.Content(tracks)
                }
            }
        }
    }
}