package com.example.playlistmaker.feature.favorite.domain.usecase

import com.example.playlistmaker.feature.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksInteractor {
    suspend fun addToFavorites(track: Track)
    suspend fun removeFromFavorites(track: Track)
    fun getFavoriteTracks(): Flow<List<Track>>
}