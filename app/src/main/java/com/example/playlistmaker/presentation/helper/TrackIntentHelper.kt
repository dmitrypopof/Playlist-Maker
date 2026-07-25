package com.example.playlistmaker.presentation.helper

import android.content.Context
import android.content.Intent
import com.example.playlistmaker.AudioPlayer
import com.example.playlistmaker.domain.model.Track

import com.google.gson.Gson

object TrackIntentHelper {
    private const val EXTRA_TRACK = "extra_track"

    fun startAudioPlayer(context: Context, track: Track) {
        val intent = Intent(context, AudioPlayer::class.java)
        val json = Gson().toJson(track)
        intent.putExtra(EXTRA_TRACK, json)
        context.startActivity(intent)
    }

    fun getTrackFromIntent(intent: Intent): Track? {
        val json = intent.getStringExtra(EXTRA_TRACK) ?: return null
        return try {
            Gson().fromJson(json, Track::class.java)
        } catch (e: Exception) {
            null
        }
    }
}