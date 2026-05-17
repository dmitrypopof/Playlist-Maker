package com.example.playlistmaker.models

import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.Locale

data class Track(
    @SerializedName("trackName") val trackName: String?,
    @SerializedName("artistName") val artistName: String?,
    @SerializedName("trackTimeMillis") val trackTime: String?,
    @SerializedName("artworkUrl100") val artworkUrl100: String?
) {
    val formattedTime: String
        get() = trackTime?.let { millis ->
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(millis)
        } ?: "00:00"


    val displayTrackName: String
        get() = trackName ?: "Unknown Track"

    val displayArtistName: String
        get() = artistName ?: "Unknown Artist"
}