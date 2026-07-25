package com.example.playlistmaker.api

import com.example.playlistmaker.data.dto.TrackResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApi  {

    @GET("/search?entity=song")
    fun search(@Query("term") text: String): Call<TrackResponse>
}