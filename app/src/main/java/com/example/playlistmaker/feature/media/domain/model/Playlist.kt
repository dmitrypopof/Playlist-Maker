package com.example.playlistmaker.feature.media.domain.model

data class Playlist(
    val playlistId: Long = 0,
    val name: String,
    val description: String?,
    val coverImagePath: String?,
    val trackIds: List<Long>,
    val tracksCount: Int
)
