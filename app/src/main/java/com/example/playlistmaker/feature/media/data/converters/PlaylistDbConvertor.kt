package com.example.playlistmaker.feature.media.data.converters

import com.example.playlistmaker.feature.media.data.db.PlaylistEntity
import com.example.playlistmaker.feature.media.domain.model.Playlist
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PlaylistDbConvertor(private val gson: Gson) {

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlistId = playlist.playlistId,
            name = playlist.name,
            description = playlist.description,
            coverImagePath = playlist.coverImagePath,
            trackIdsJson = gson.toJson(playlist.trackIds),
            tracksCount = playlist.tracksCount
        )
    }

    fun map(entity: PlaylistEntity): Playlist {
        val type = object : TypeToken<List<Long>>() {}.type
        val trackIds: List<Long> = try {
            gson.fromJson(entity.trackIdsJson, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }

        return Playlist(
            playlistId = entity.playlistId,
            name = entity.name,
            description = entity.description,
            coverImagePath = entity.coverImagePath,
            trackIds = trackIds,
            tracksCount = entity.tracksCount
        )
    }
}