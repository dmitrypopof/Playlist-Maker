package com.example.playlistmaker.feature.player.presentation

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.feature.search.domain.model.Track
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerViewModel : ViewModel() {

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }

    private var mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT

    private val mainThreadHandler = Handler(Looper.getMainLooper())
    private var updateProgressRunnable: Runnable? = null

    // Состояние экрана
    private val _state = MutableLiveData<AudioPlayerState>()
    val state: LiveData<AudioPlayerState> = _state

    // Текущий трек
    private var currentTrack: Track? = null

    /**
     * Загрузка трека для воспроизведения
     */
    fun loadTrack(track: Track) {
        currentTrack = track
        _state.value = AudioPlayerState.Content(track)
        preparePlayer(track)
    }

    /**
     * Подготовка плеера
     */
    private fun preparePlayer(track: Track) {
        val previewUrl = track.previewUrl
        if (previewUrl == null) {
            return
        }

        mediaPlayer.apply {
            setDataSource(previewUrl)
            prepareAsync()
            setOnPreparedListener {
                playerState = STATE_PREPARED
                _state.value = AudioPlayerState.Prepared
            }
            setOnCompletionListener {
                playerState = STATE_PREPARED
                _state.value = AudioPlayerState.Prepared
                stopUpdateProgress()
            }
        }
    }

    /**
     * Управление воспроизведением (play/pause)
     */
    fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> pausePlayer()
            STATE_PREPARED, STATE_PAUSED -> startPlayer()
        }
    }

    /**
     * Запуск воспроизведения
     */
    private fun startPlayer() {
        mediaPlayer.apply {
            start()
            playerState = STATE_PLAYING
            _state.value = AudioPlayerState.Playing
            startUpdateProgress()
        }
    }

    /**
     * Пауза воспроизведения
     */
    private fun pausePlayer() {
        if (playerState == STATE_PLAYING) {
            mediaPlayer.pause()
            playerState = STATE_PAUSED
            _state.value = AudioPlayerState.Paused
            stopUpdateProgress()
        }
    }

    /**
     * Запуск обновления прогресса
     */
    private fun startUpdateProgress() {
        updateProgressRunnable = object : Runnable {
            override fun run() {
                if (playerState == STATE_PLAYING) {
                    val currentPosition = mediaPlayer.currentPosition
                    val formattedTime = SimpleDateFormat("mm:ss", Locale.getDefault())
                        .format(currentPosition)
                    _state.value = AudioPlayerState.Progress(formattedTime)
                    mainThreadHandler.postDelayed(this, 300)
                }
            }
        }
        updateProgressRunnable?.let {
            mainThreadHandler.post(it)
        }
    }

    /**
     * Остановка обновления прогресса
     */
    private fun stopUpdateProgress() {
        updateProgressRunnable?.let {
            mainThreadHandler.removeCallbacks(it)
        }
        updateProgressRunnable = null
    }

    /**
     * Получение форматированного времени трека
     */
    fun getTrackFormattedTime(): String {
        return currentTrack?.formattedTime ?: "00:00"
    }

    /**
     * Получение времени в миллисекундах для отображения
     */
    fun getCurrentPosition(): String {
        return if (playerState == STATE_PLAYING || playerState == STATE_PAUSED) {
            SimpleDateFormat("mm:ss", Locale.getDefault())
                .format(mediaPlayer.currentPosition)
        } else {
            currentTrack?.formattedTime ?: "00:00"
        }
    }

    /**
     * Освобождение ресурсов
     */
    override fun onCleared() {
        super.onCleared()
        stopUpdateProgress()
        mediaPlayer.release()
    }

    /**
     * Приостановка воспроизведения при паузе Activity
     */
    fun onPause() {
        if (playerState == STATE_PLAYING) {
            pausePlayer()
        }
    }
}