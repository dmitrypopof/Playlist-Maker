package com.example.playlistmaker.feature.favorite.data.repository

import com.example.playlistmaker.feature.favorite.domain.repository.FavoriteTracksRepository
import com.example.playlistmaker.feature.favorite.data.converters.FavoriteTrackDbConvertor
import com.example.playlistmaker.feature.favorite.data.db.AppDatabase
import com.example.playlistmaker.feature.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteTracksRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val convertor: FavoriteTrackDbConvertor
) : FavoriteTracksRepository {

    override suspend fun addToFavorites(track: Track) {
        appDatabase.favoriteTrackDao().insertTrack(convertor.map(track))
    }

    override suspend fun removeFromFavorites(track: Track) {
        appDatabase.favoriteTrackDao().deleteTrack(convertor.map(track))
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return appDatabase.favoriteTrackDao().getFavoriteTracks().map { entities ->
            entities.map { entity -> convertor.map(entity) }
        }
    }
}