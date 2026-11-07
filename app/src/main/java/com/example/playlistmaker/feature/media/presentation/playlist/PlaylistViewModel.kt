package com.example.playlistmaker.feature.media.presentation.playlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.feature.media.domain.usecase.PlaylistsInteractor
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistViewModel(
    private val playlistId: Long,
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    private val _state = MutableLiveData<PlaylistState>(PlaylistState.Loading)
    val state: LiveData<PlaylistState> = _state

    init {
        loadPlaylist()
    }

    private fun loadPlaylist() {
        viewModelScope.launch {
            val playlist = playlistsInteractor.getPlaylistById(playlistId)

            if (playlist == null) {
                return@launch
            }

            playlistsInteractor.getTracksByIds(playlist.trackIds)
                .onEach { tracks ->
                    val durationSum = tracks.sumOf { it.trackTime }
                    val formattedDuration = SimpleDateFormat(
                        "mm",
                        Locale.getDefault()
                    ).format(durationSum)

                    _state.value = PlaylistState.Content(
                        playlist = playlist,
                        tracks = tracks,
                        totalDurationMinutes = formattedDuration
                    )
                }
                .launchIn(viewModelScope)
        }
    }
}