package com.example.playlistmaker.feature.search.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.feature.search.domain.model.Track
import com.example.playlistmaker.feature.search.domain.repository.SearchHistoryRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistoryRepositoryImpl(
    private val context: Context
) : SearchHistoryRepository {
    private val prefs: SharedPreferences = context.getSharedPreferences("history_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val maxSize = 10

    override fun getHistory(): List<Track> {
        val json = prefs.getString("history_json", null)
        return if (json != null) {
            val type = object : TypeToken<List<Track>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } else {
            emptyList()
        }
    }

    override fun addTrack(track: Track) {
        val history = getHistory().toMutableList()
        history.removeAll { it.trackName == track.trackName && it.artistName == track.artistName }
        history.add(0, track)
        if (history.size > maxSize) {
            history.removeAt(history.size - 1)
        }
        saveHistory(history)
    }

    override fun clearHistory() {
        saveHistory(emptyList())
    }

    private fun saveHistory(history: List<Track>) {
        val json = gson.toJson(history)
        prefs.edit().putString("history_json", json).apply()
    }
}