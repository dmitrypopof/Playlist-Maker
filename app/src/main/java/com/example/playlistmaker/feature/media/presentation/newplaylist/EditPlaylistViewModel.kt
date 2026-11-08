package com.example.playlistmaker.feature.media.presentation.newplaylist

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.feature.media.domain.model.Playlist
import com.example.playlistmaker.feature.media.domain.usecase.PlaylistsInteractor
import kotlinx.coroutines.launch

class EditPlaylistViewModel(
    private val playlistId: Long,
    playlistsInteractor: PlaylistsInteractor
) : NewPlaylistViewModel(playlistsInteractor) {

    private var originalPlaylist: Playlist? = null

    private val _initialData = MutableLiveData<Playlist>()
    val initialData: LiveData<Playlist> = _initialData

    init {
        viewModelScope.launch {
            val loadedPlaylist = playlistsInteractor.getPlaylistById(playlistId) ?: return@launch

                originalPlaylist = loadedPlaylist
                name = loadedPlaylist.name
                description = loadedPlaylist.description.orEmpty()
                coverUri = loadedPlaylist.coverImagePath?.let { Uri.parse(it) }
                updateCreateButtonState()
                _initialData.value = loadedPlaylist

        }
    }

    override fun updateCreateButtonState() {
        _isCreateButtonEnabled.value = name.trim().isNotEmpty()
    }

    override fun createPlaylist() {

        val playlist = originalPlaylist ?: return
        val playlistName = name.trim()
        if (playlistName.isEmpty()) return

        viewModelScope.launch {
            val coverPath = if (coverUri != null) {
                if (coverUri.toString() == playlist.coverImagePath) {
                    playlist.coverImagePath
                } else {
                    playlistsInteractor.saveCoverToPrivateStorage(coverUri!!)
                }
            } else {
                null
            }

            val updatedPlaylist = playlist.copy(
                name = playlistName,
                description = description.trim().ifEmpty { null },
                coverImagePath = coverPath
            )

            playlistsInteractor.updatePlaylist(updatedPlaylist)
            _createdPlaylistEvent.value = playlistName
        }
    }
}