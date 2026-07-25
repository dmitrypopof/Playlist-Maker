package com.example.playlistmaker.domain.model

import java.text.SimpleDateFormat
import java.util.Locale

data class Track (
    val trackName: String,
    val artistName: String,
    val trackTime: Long,
    val artworkUrl100: String,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String?
) {
    val formattedTime: String
        get() = SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTime)

    val displayYear: String
        get() = releaseDate.take(4)

    companion object {
        fun createDefault(): Track {
            return Track(
                trackName = "Unknown Track",
                artistName = "Unknown Artist",
                trackTime = 0,
                artworkUrl100 = "",
                collectionName = "Unknown Album",
                releaseDate = "Unknown",
                primaryGenreName = "Unknown",
                country = "Unknown",
                previewUrl = null
            )
        }
    }
}