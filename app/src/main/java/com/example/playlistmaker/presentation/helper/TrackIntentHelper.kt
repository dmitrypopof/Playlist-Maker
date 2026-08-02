package com.example.playlistmaker.presentation.helper

import android.content.Intent
import com.example.playlistmaker.domain.model.Track
import com.google.gson.Gson

object TrackIntentHelper {
    private const val EXTRA_TRACK = "extra_track"

    fun getTrackFromIntent(intent: Intent): Track? {
        val json = intent.getStringExtra(EXTRA_TRACK) ?: return null
        return try {
            Gson().fromJson(json, Track::class.java)
        } catch (e: Exception) {
            android.util.Log.e("TrackIntentHelper", "Failed to parse track from JSON", e)
            null
        }
    }
    fun putTrackToIntent(intent: Intent, track: Track) {
        val json = Gson().toJson(track)
        intent.putExtra(EXTRA_TRACK, json)
    }
}