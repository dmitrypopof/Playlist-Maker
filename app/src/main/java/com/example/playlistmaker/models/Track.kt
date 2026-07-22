package com.example.playlistmaker.models

import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.Locale

data class Track(
    @SerializedName("trackName") val trackName: String?,
    @SerializedName("artistName") val artistName: String?,
    @SerializedName("trackTimeMillis") val trackTime: Long?,
    @SerializedName("artworkUrl100") val artworkUrl100: String?,
    @SerializedName("collectionName") val collectionName: String? = null,
    @SerializedName("releaseDate") val releaseDate: String? = null,
    @SerializedName("primaryGenreName") val primaryGenreName: String? = null,
    @SerializedName("country") val country: String? = null,
    @SerializedName("previewUrl") val previewUrl: String? = null
) {
    val formattedTime: String
        get() = trackTime?.let { millis ->
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(millis)
        } ?: "00:00"


    val displayTrackName: String
        get() = trackName ?: "Unknown Track"

    val displayArtistName: String
        get() = artistName ?: "Unknown Artist"

    val displayCollectionName: String
        get() = collectionName ?: "Unknown Album"

    val displayYear: String
        get() = releaseDate?.take(4) ?: "Unknown"

    val displayGenre: String
        get() = primaryGenreName ?: "Unknown"

    val displayCountry: String
        get() = country ?: "Unknown"
}