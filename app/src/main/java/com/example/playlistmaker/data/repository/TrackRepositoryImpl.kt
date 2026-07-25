package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.network.RetrofitHelper
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.TrackRepository


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