package com.example.playlistmaker.feature.media.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PlaylistTrackDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(track: PlaylistTrackEntity)

    @Query("SELECT * FROM playlist_tracks_table WHERE trackId IN (:trackIds)")
    suspend fun getTracksByIds(trackIds: List<Long>): List<PlaylistTrackEntity>
}