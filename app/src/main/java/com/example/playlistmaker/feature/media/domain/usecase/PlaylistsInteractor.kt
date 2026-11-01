package com.example.playlistmaker.feature.media.domain.usecase

import android.net.Uri
import com.example.playlistmaker.feature.media.domain.model.Playlist
import com.example.playlistmaker.feature.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsInteractor {
    suspend fun createPlaylist(playlist: Playlist): Long

    suspend fun updatePlaylist(playlist: Playlist)

    fun getPlaylists(): Flow<List<Playlist>>

    suspend fun getPlaylistById(playlistId: Long): Playlist?

    suspend fun addTrackToPlaylist(track: Track, playlist: Playlist)

    fun saveCoverToPrivateStorage(uri: Uri): String?
}