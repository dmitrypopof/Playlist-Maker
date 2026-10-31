package com.example.playlistmaker.feature.search.data.repository

import com.example.playlistmaker.feature.favorite.data.db.AppDatabase
import com.example.playlistmaker.feature.search.data.mapper.TrackMapper
import com.example.playlistmaker.feature.search.data.network.ITunesApi
import com.example.playlistmaker.feature.search.domain.model.Track
import com.example.playlistmaker.feature.search.domain.repository.TrackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.coroutines.cancellation.CancellationException

class TrackRepositoryImpl(
    private val api: ITunesApi,
    private val appDatabase: AppDatabase
) : TrackRepository {

    override fun searchTracks(query: String): Flow<Result<List<Track>>> = flow {
        emit(makeRequest(query))
    }

    private suspend fun makeRequest(query: String): Result<List<Track>> {
        return try {
            val response = api.search(query)
            val tracks = response.results.map { TrackMapper.map(it) }
            markFavoriteTracks(tracks)
            Result.success(tracks)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun markFavoriteTracks(tracks: List<Track>) {
        val favoriteIds = appDatabase.favoriteTrackDao().getFavoriteTrackIds()
        tracks.forEach { track ->
            track.isFavorite = favoriteIds.contains(track.trackId)
        }
    }
}