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
    private var mediaPlayer: MediaPlayer? = null
    private var playerState = PlayerState.DEFAULT

    private val mainThreadHandler = Handler(Looper.getMainLooper())
    private var updateProgressRunnable: Runnable? = null

    // Состояние экрана
    private val _state = MutableLiveData<AudioPlayerState>()
    val state: LiveData<AudioPlayerState> = _state

    // Текущий трек
    private var currentTrack: Track? = null

    //Загрузка трека для воспроизведения
    fun loadTrack(track: Track) {
        currentTrack = track
        _state.value = AudioPlayerState.Content(track)
        preparePlayer(track)
    }

     // Подготовка плеера
    private fun preparePlayer(track: Track) {
        val previewUrl = track.previewUrl
        if (previewUrl == null) {
            return
        }


    releasePlayer()

         mediaPlayer = MediaPlayer().apply {
             setDataSource(previewUrl)
             prepareAsync()
             setOnPreparedListener {
                 playerState = PlayerState.PREPARED
                 _state.value = AudioPlayerState.Prepared
             }
             setOnCompletionListener {
                 playerState = PlayerState.PREPARED
                 _state.value = AudioPlayerState.Prepared
                 stopUpdateProgress()
             }
         }
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
         mediaPlayer?.let { player ->
             player.start()
             playerState = PlayerState.PLAYING
             _state.value = AudioPlayerState.Playing
             startUpdateProgress()
         }
    }

      //Пауза воспроизведения
    private fun pausePlayer() {
        if (playerState == PlayerState.PLAYING) {
            mediaPlayer?.pause()
            playerState = PlayerState.PAUSED
            _state.value = AudioPlayerState.Paused
            stopUpdateProgress()
        }
    }

    //Запуск обновления прогресса
    private fun startUpdateProgress() {
        updateProgressRunnable = object : Runnable {
            override fun run(){
                if (playerState == PlayerState.PLAYING) {
                    mediaPlayer?.let { player ->
                        val currentPosition = player.currentPosition
                        val formattedTime = SimpleDateFormat("mm:ss", Locale.getDefault())
                            .format(currentPosition)
                        _state.value = AudioPlayerState.Progress(formattedTime)
                    }
                    mainThreadHandler.postDelayed(this, 300)
                }
            }
        }
        updateProgressRunnable?.let {
            mainThreadHandler.post(it)
        }
    }

     //Остановка обновления прогресса
    private fun stopUpdateProgress() {
        updateProgressRunnable?.let {
            mainThreadHandler.removeCallbacks(it)
        }
        updateProgressRunnable = null
    }

    //Получение текущего трека
//    fun getCurrentTrack(): Track? {
//        return currentTrack
//    }

    // Освобождение ресурсов
    private fun releasePlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
        playerState = PlayerState.DEFAULT
    }

    //Приостановка воспроизведения при паузе Activity
    fun onPause() {
        if (playerState == PlayerState.PLAYING) {
            pausePlayer()
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopUpdateProgress()
        releasePlayer()
    }
}