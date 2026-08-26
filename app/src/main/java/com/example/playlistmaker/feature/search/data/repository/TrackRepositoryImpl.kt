package com.example.playlistmaker.feature.search.data.repository

import com.example.playlistmaker.feature.search.data.network.RetrofitHelper
import com.example.playlistmaker.feature.search.data.mapper.TrackMapper
import com.example.playlistmaker.feature.search.domain.model.Track
import com.example.playlistmaker.feature.search.domain.repository.TrackRepository

class TrackRepositoryImpl: TrackRepository {
    override fun searchTracks(query: String): Result<List<Track>> {
        return try {
            val response = RetrofitHelper.apiService.search(query).execute()
            if (response.isSuccessful) {
                val tracks = response.body()?.results?.map { TrackMapper.map(it) } ?: emptyList()
                Result.success(tracks)
            } else {
                Result.failure(Exception("Server error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}