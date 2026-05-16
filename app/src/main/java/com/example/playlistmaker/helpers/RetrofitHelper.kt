package com.example.playlistmaker.helpers

import com.example.playlistmaker.api.ITunesApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.jvm.java

class RetrofitHelper {
    private val BASE_URL = "https://itunes.apple.com"

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())  // для парсинга JSON
            .build()
    }

    val apiService: ITunesApi by lazy {
        retrofit.create(ITunesApi::class.java)
    }
}