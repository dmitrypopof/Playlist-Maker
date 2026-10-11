package com.example.playlistmaker.feature.search.data.repository

import com.example.playlistmaker.feature.search.data.mapper.TrackMapper
import com.example.playlistmaker.feature.search.data.network.ITunesApi
import com.example.playlistmaker.feature.search.domain.model.Track
import com.example.playlistmaker.feature.search.domain.repository.TrackRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class TrackRepositoryImpl(
    private val api: ITunesApi
) : TrackRepository {

    override fun searchTracks(query: String): Flow<Result<List<Track>>> = flow {
        emit(makeRequest(query))
    }

    private suspend fun makeRequest(query: String): Result<List<Track>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.search(query)
                val tracks = response.results.map { TrackMapper.map(it) }
                Result.success(tracks)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}