package com.example.playlistmaker.feature.player.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.feature.favorite.domain.usecase.FavoriteTracksInteractor
import com.example.playlistmaker.feature.player.domain.repository.PlayerRepository
import com.example.playlistmaker.feature.search.domain.model.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerViewModel(
    private val playerRepository: PlayerRepository,
    private val favoriteTracksInteractor: FavoriteTracksInteractor
) : ViewModel() {

    companion object {
        private const val PROGRESS_UPDATE_DELAY_MS = 300L
        private const val DEFAULT_TIME = "00:00"
    }

    private var playerState = PlayerState.DEFAULT

    private var progressJob: Job? = null

    // Состояние экрана
    private val _state = MutableLiveData<AudioPlayerState>()
    val state: LiveData<AudioPlayerState> = _state

    // LiveData для статуса избранного
    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> = _isFavorite

    // Текущий трек
    private var currentTrack: Track? = null

    //Загрузка трека для воспроизведения
    fun loadTrack(track: Track) {
        currentTrack = track
        _state.value = AudioPlayerState.Content(track)
        _isFavorite.value = track.isFavorite
        preparePlayer(track)
    }

    // Подготовка плеера
    private fun preparePlayer(track: Track) {
        val previewUrl = track.previewUrl ?: return

        playerRepository.preparePlayer(
            url = previewUrl,
            onPrepared = {
                playerState = PlayerState.PREPARED
                _state.value = AudioPlayerState.Prepared
            },
            onCompletion = {
                playerState = PlayerState.PREPARED
                stopUpdateProgress()
                _state.value = AudioPlayerState.Prepared
                _state.value = AudioPlayerState.Progress(DEFAULT_TIME)
            }
        )
    }

    //Управление воспроизведением (play/pause)
    fun playbackControl() {
        when (playerState) {
            PlayerState.PLAYING -> pausePlayer()
            PlayerState.PREPARED, PlayerState.PAUSED -> startPlayer()
            PlayerState.DEFAULT -> {
                // В состоянии DEFAULT ничего не делаем
            }
        }
    }

    //Запуск воспроизведения
    private fun startPlayer() {
        playerRepository.start()
        playerState = PlayerState.PLAYING
        _state.value = AudioPlayerState.Playing
        startUpdateProgress()
    }

    //Пауза воспроизведения
    private fun pausePlayer() {
        if (playerState == PlayerState.PLAYING) {
            playerRepository.pause()
            playerState = PlayerState.PAUSED
            _state.value = AudioPlayerState.Paused
            stopUpdateProgress()
        }
    }

    //Запуск обновления прогресса
    private fun startUpdateProgress() {
        progressJob = viewModelScope.launch {
            while (playerState == PlayerState.PLAYING) {
                val currentPosition = playerRepository.getCurrentPosition()
                val formattedTime =
                    SimpleDateFormat("mm:ss", Locale.getDefault()).format(currentPosition)
                _state.value = AudioPlayerState.Progress(formattedTime)
                delay(PROGRESS_UPDATE_DELAY_MS)
            }
        }
    }

    //Остановка обновления прогресса
    private fun stopUpdateProgress() {
        progressJob?.cancel()
        progressJob = null
    }

    //Получение текущего трека
//    fun getCurrentTrack(): Track? {
//        return currentTrack
//    }

    // Освобождение ресурсов
    private fun releasePlayer() {
        playerRepository.release()
        playerState = PlayerState.DEFAULT
    }

    //Приостановка воспроизведения при паузе Activity
    fun onPause() {
        if (playerState == PlayerState.PLAYING) {
            pausePlayer()
        }
    }

    // обработка нажатия на кнопку "Нравится"
    fun onFavoriteClicked() {
        val track = currentTrack ?: return

        viewModelScope.launch {
            if (track.isFavorite) {
                favoriteTracksInteractor.removeFromFavorites(track)
                track.isFavorite = false
            } else {
                favoriteTracksInteractor.addToFavorites(track)
                track.isFavorite = true
            }
            _isFavorite.value = track.isFavorite
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopUpdateProgress()
        releasePlayer()
    }
}