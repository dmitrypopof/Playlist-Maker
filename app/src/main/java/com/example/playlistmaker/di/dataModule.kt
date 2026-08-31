package com.example.playlistmaker.di

import android.content.Context
import com.example.playlistmaker.feature.search.data.network.ITunesApi
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    single<ITunesApi> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesApi::class.java)
    }

    single(named("history_prefs")) {
        androidContext()
            .getSharedPreferences("history_prefs", Context.MODE_PRIVATE)
    }

    single(named("app_settings")) {
        androidContext()
            .getSharedPreferences("app_settings", Context.MODE_PRIVATE)
    }

    single { Gson() }
}