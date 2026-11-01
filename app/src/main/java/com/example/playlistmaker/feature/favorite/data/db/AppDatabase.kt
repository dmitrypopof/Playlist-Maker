package com.example.playlistmaker.feature.favorite.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.feature.media.data.db.PlaylistDao
import com.example.playlistmaker.feature.media.data.db.PlaylistEntity
import com.example.playlistmaker.feature.media.data.db.PlaylistTrackDao
import com.example.playlistmaker.feature.media.data.db.PlaylistTrackEntity


@Database(
    version = 2,
    entities = [
        FavoriteTrackEntity::class,
        PlaylistEntity::class,
        PlaylistTrackEntity::class
    ]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteTrackDao(): FavoriteTrackDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun playlistTrackDao(): PlaylistTrackDao
}