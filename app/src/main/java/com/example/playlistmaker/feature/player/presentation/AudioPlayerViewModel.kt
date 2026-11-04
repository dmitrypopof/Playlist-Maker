package com.example.playlistmaker.feature.player.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.feature.favorite.domain.usecase.FavoriteTracksInteractor
import com.example.playlistmaker.feature.media.domain.model.Playlist
import com.example.playlistmaker.feature.media.domain.usecase.PlaylistsInteractor
import com.example.playlistmaker.feature.player.domain.repository.PlayerRepository
import com.example.playlistmaker.feature.search.domain.model.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerViewModel(
    private val playerRepository: PlayerRepository,
    private val favoriteTracksInteractor: FavoriteTracksInteractor,
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    companion object {
        private const val PROGRESS_UPDATE_DELAY_MS = 300L
        private const val DEFAULT_TIME = "00:00"
    }

    private var playerState = PlayerState.DEFAULT

    private var progressJob: Job? = null
    private var favoriteJob: Job? = null

    // Состояние экрана
    private val _state = MutableLiveData<AudioPlayerState>()
    val state: LiveData<AudioPlayerState> = _state

    // LiveData для статуса избранного
    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> = _isFavorite

    //LiveData со списком плейлистов для Bottom Sheet
    private val _playlists = MutableLiveData<List<Playlist>>(emptyList())
    val playlists: LiveData<List<Playlist>> = _playlists

    //LiveData-событие результата добавления трека в плейлист
    private val _addToPlaylistResult = MutableLiveData<AudioPlayerAddToPlaylistResult?>()
    val addToPlaylistResult: LiveData<AudioPlayerAddToPlaylistResult?> = _addToPlaylistResult

    // Текущий трек
    private var currentTrack: Track? = null

    init {
        observePlaylists()
    }

    // подписка на актуальный список плейлистов из БД
    private fun observePlaylists() {
        playlistsInteractor.getPlaylists()
            .onEach { playlistsList ->
                _playlists.value = playlistsList
            }
            .launchIn(viewModelScope)
    }

    //Загрузка трека для воспроизведения
    fun loadTrack(track: Track) {
        currentTrack = track
        _state.value = AudioPlayerState.Content(track)
        observeFavoriteStatus(track.trackId)
        preparePlayer(track)
    }
    //подписка на актуальный список избранного
    private fun observeFavoriteStatus(trackId: Long) {
        favoriteJob?.cancel()
        favoriteJob = viewModelScope.launch {
            favoriteTracksInteractor.getFavoriteTracks().collect { favorites ->
                _isFavorite.value = favorites.any { it.trackId == trackId }
            }
        }
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
            val currentlyFavorite = _isFavorite.value ?: false
            if (currentlyFavorite) {
                favoriteTracksInteractor.removeFromFavorites(track)
            } else {
                favoriteTracksInteractor.addToFavorites(track)
            }
        }
    }

    //обработка нажатия на плейлист в Bottom Sheet
    fun onPlaylistSelected(playlist: Playlist) {
        val track = currentTrack ?: return

        val alreadyInPlaylist = playlist.trackIds.contains(track.trackId)
        if (alreadyInPlaylist) {
            _addToPlaylistResult.value = AudioPlayerAddToPlaylistResult.AlreadyAdded(playlist.name)
            return
        }

        viewModelScope.launch {
            playlistsInteractor.addTrackToPlaylist(track, playlist)
            _addToPlaylistResult.value = AudioPlayerAddToPlaylistResult.Added(playlist.name)
        }
    }

    // сброс одноразового события после обработки в UI
    fun onAddToPlaylistResultHandled() {
        _addToPlaylistResult.value = null
    }

    override fun onCleared() {
        super.onCleared()
        stopUpdateProgress()
        favoriteJob?.cancel()
        releasePlayer()
    }
}