package com.example.playlistmaker.feature.media.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import com.example.playlistmaker.feature.media.data.converters.PlaylistDbConvertor
import com.example.playlistmaker.feature.media.data.converters.PlaylistTrackDbConvertor
import com.example.playlistmaker.feature.media.data.db.PlaylistDao
import com.example.playlistmaker.feature.media.data.db.PlaylistTrackDao
import com.example.playlistmaker.feature.media.domain.model.Playlist
import com.example.playlistmaker.feature.media.domain.repository.PlaylistsRepository
import com.example.playlistmaker.feature.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class PlaylistsRepositoryImpl(
    private val playlistDao: PlaylistDao,
    private val playlistTrackDao: PlaylistTrackDao,
    private val playlistDbConvertor: PlaylistDbConvertor,
    private val playlistTrackDbConvertor: PlaylistTrackDbConvertor,
    private val context: Context
) : PlaylistsRepository {

    companion object {
        private const val ALBUM_DIR_NAME = "playlist_covers"
        private const val JPEG_QUALITY = 100
    }

    override suspend fun createPlaylist(playlist: Playlist): Long {
        return playlistDao.insertPlaylist(playlistDbConvertor.map(playlist))
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistDao.updatePlaylist(playlistDbConvertor.map(playlist))
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return playlistDao.getPlaylists().map { entities ->
            entities.map { playlistDbConvertor.map(it) }
        }
    }

    override suspend fun getPlaylistById(playlistId: Long): Playlist? {
        return playlistDao.getPlaylistById(playlistId)?.let { playlistDbConvertor.map(it) }
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        val updatedTrackIds = playlist.trackIds + track.trackId
        val updatedPlaylist = playlist.copy(
            trackIds = updatedTrackIds,
            tracksCount = updatedTrackIds.size
        )
        playlistDao.updatePlaylist(playlistDbConvertor.map(updatedPlaylist))
        playlistTrackDao.insertTrack(playlistTrackDbConvertor.map(track))
    }

    override fun saveCoverToPrivateStorage(uri: Uri): String? {
        return try {
            val dir =
                File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), ALBUM_DIR_NAME)
            if (!dir.exists()) {
                dir.mkdirs()
            }

            val fileName = "${UUID.randomUUID()}.jpg"
            val file = File(dir, fileName)

            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, outputStream)
            outputStream.flush()
            outputStream.close()

            file.absolutePath
        } catch (e: Exception) {
            null
        }
    }

    override fun getTracksByIds(trackIds: List<Long>): Flow<List<Track>> = flow {
        val allTracksEntities = playlistTrackDao.getAllTracks()
        val allTracks = allTracksEntities.map { playlistTrackDbConvertor.map(it) }
        val filteredTracks = trackIds.mapNotNull { id ->
            allTracks.find { it.trackId == id }
        }
        emit(filteredTracks)
    }

    override suspend fun removeTrackFromPlaylist(trackId: Long, playlist: Playlist) {
        val updatedTrackIds = playlist.trackIds.filterNot { it == trackId }
        val updatedPlaylist = playlist.copy(
            trackIds = updatedTrackIds,
            tracksCount = updatedTrackIds.size
        )
        playlistDao.updatePlaylist(playlistDbConvertor.map(updatedPlaylist))

        removeTrackIfUnused(trackId)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        playlistDao.deletePlaylistById(playlist.playlistId)

        val remainingPlaylists = playlistDao.getPlaylists().first()
        val usedTrackIds = remainingPlaylists
            .flatMap { playlistDbConvertor.map(it).trackIds }
            .toHashSet()

        playlist.trackIds.forEach { trackId ->
            if (trackId !in usedTrackIds) {
                playlistTrackDao.deleteTrackById(trackId)
            }
        }
    }

    private suspend fun removeTrackIfUnused(trackId: Long) {
        val allPlaylists = playlistDao.getPlaylists().first()
        val isUsed = allPlaylists.any { entity ->
            playlistDbConvertor.map(entity).trackIds.contains(trackId)
        }
        if (!isUsed) {
            playlistTrackDao.deleteTrackById(trackId)
        }
    }
}