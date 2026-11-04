package com.example.playlistmaker.feature.media.presentation.newplaylist

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.feature.media.domain.model.Playlist
import com.example.playlistmaker.feature.media.domain.usecase.PlaylistsInteractor
import kotlinx.coroutines.launch

class NewPlaylistViewModel(
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    private var coverUri: Uri? = null
    private var name: String = ""
    private var description: String = ""

    private val _isCreateButtonEnabled = MutableLiveData(false)
    val isCreateButtonEnabled: LiveData<Boolean> = _isCreateButtonEnabled

    private val _coverPreview = MutableLiveData<Uri?>()
    val coverPreview: LiveData<Uri?> = _coverPreview

    private val _createdPlaylistEvent = MutableLiveData<String?>()
    val createdPlaylistEvent: LiveData<String?> = _createdPlaylistEvent

    fun onNameChanged(newName: String) {
        name = newName
        updateCreateButtonState()
    }

    fun onDescriptionChanged(newDescription: String) {
        description = newDescription
    }

    fun onCoverSelected(uri: Uri) {
        coverUri = uri
        _coverPreview.value = uri
    }

    fun hasUnsavedData(): Boolean {
        return coverUri != null || name.isNotBlank() || description.isNotBlank()
    }

    fun createPlaylist() {
        val playlistName = name.trim()
        if (playlistName.isEmpty()) return

        viewModelScope.launch {
            val coverPath = coverUri?.let { uri ->
                playlistsInteractor.saveCoverToPrivateStorage(uri)
            }

            val playlist = Playlist(
                name = playlistName,
                description = description.trim().ifEmpty { null },
                coverImagePath = coverPath,
                trackIds = emptyList(),
                tracksCount = 0
            )

            playlistsInteractor.createPlaylist(playlist)
            _createdPlaylistEvent.value = playlistName
        }
    }

    fun onCreatedEventHandled() {
        _createdPlaylistEvent.value = null
    }

    private fun updateCreateButtonState() {
        _isCreateButtonEnabled.value = name.trim().isNotEmpty()
    }
}