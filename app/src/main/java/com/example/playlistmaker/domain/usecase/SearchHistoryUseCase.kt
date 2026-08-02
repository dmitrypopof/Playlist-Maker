package com.example.playlistmaker.domain.usecase

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.domain.model.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistoryUseCase(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("history_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    private val history = mutableListOf<Track>()
    private val maxSize = 10

    init {
        // Загружаем историю при создании объекта
        loadHistory()
    }

    fun getHistory(): List<Track> {
        return history.toList()
    }

    fun addTrack(track: Track) {
        history.removeAll { it.trackName == track.trackName && it.artistName == track.artistName }
        history.add(0, track)
        if (history.size > maxSize) {
            history.removeAt(history.size - 1)
        }
        saveHistory()
    }

    fun clearHistory() {
        history.clear()
        saveHistory()
    }

    private fun saveHistory() {
        val json = gson.toJson(history)
        prefs.edit().putString("history_json", json).apply()
    }
    private fun loadHistory() {
        val json = prefs.getString("history_json", null)
        if (json != null) {
            val type = object : TypeToken<List<Track>>() {}.type
            val loaded: List<Track> = gson.fromJson(json, type)
            history.clear()
            history.addAll(loaded)
        }
    }
}