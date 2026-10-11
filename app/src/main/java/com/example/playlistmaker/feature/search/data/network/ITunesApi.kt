package com.example.playlistmaker.feature.search.data.network

import com.example.playlistmaker.feature.search.data.network.dto.TrackResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApi  {

    @GET("/search?entity=song")
    suspend fun search(@Query("term") text: String): TrackResponse
}