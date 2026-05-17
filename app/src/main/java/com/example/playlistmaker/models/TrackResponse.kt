package com.example.playlistmaker.models

import com.google.gson.annotations.SerializedName

data class TrackResponse(
    @SerializedName("resultCount") val resultCount: Int,
    @SerializedName("results") val results: List<Track>
)
