package com.example.playlistmaker.feature.favorite.domain.usecase

import com.example.playlistmaker.feature.favorite.domain.repository.FavoriteTracksRepository
import com.example.playlistmaker.feature.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

class FavoriteTracksInteractorImpl(
    private val repository: FavoriteTracksRepository
) : FavoriteTracksInteractor {

    override suspend fun addToFavorites(track: Track) {
        repository.addToFavorites(track)
    }

    override suspend fun removeFromFavorites(track: Track) {
        repository.removeFromFavorites(track)
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return repository.getFavoriteTracks()
    }
}