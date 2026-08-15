package com.example.playlistmaker.core.network

import com.example.playlistmaker.core.network.dto.TrackResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApi  {

    @GET("/search?entity=song")
    fun search(@Query("term") text: String): Call<TrackResponse>
}