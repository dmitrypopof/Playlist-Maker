package com.example.playlistmaker.core.network.dto

import com.google.gson.annotations.SerializedName

data class TrackResponse(
    @SerializedName("resultCount") val resultCount: Int,
    @SerializedName("results") val results: List<TrackDto>
)