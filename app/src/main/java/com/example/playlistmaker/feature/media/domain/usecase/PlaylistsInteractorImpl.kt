package com.example.playlistmaker.feature.media.domain.usecase

import android.net.Uri
import com.example.playlistmaker.feature.media.domain.model.Playlist
import com.example.playlistmaker.feature.media.domain.repository.PlaylistsRepository
import com.example.playlistmaker.feature.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

class PlaylistsInteractorImpl(
    private val repository: PlaylistsRepository
) : PlaylistsInteractor {

    override suspend fun createPlaylist(playlist: Playlist): Long {
        return repository.createPlaylist(playlist)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        repository.updatePlaylist(playlist)
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return repository.getPlaylists()
    }

    override suspend fun getPlaylistById(playlistId: Long): Playlist? {
        return repository.getPlaylistById(playlistId)
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        repository.addTrackToPlaylist(track, playlist)
    }

    override fun saveCoverToPrivateStorage(uri: Uri): String? {
        return repository.saveCoverToPrivateStorage(uri)
    }
}