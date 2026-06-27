package com.example.playlistmaker

import android.content.Context
import com.example.playlistmaker.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(private val context: Context)  {

    private val prefs = context.getSharedPreferences("search_history", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val HISTORY_KEY = "track_history"
        private const val MAX_HISTORY_SIZE = 10
    }

    // Получить всю историю
    fun getHistory(): List<Track> {
        val json = prefs.getString(HISTORY_KEY, null) ?: return emptyList()
        val type = object : TypeToken<List<Track>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    // Добавить трек в историю
    fun addTrack(track: Track) {
        val history = getHistory().toMutableList()

        // Удаляем трек, если он уже есть в истории (чтобы не дублировался)
        history.removeAll { it.trackName == track.trackName && it.artistName == track.artistName }

        // Добавляем трек в начало списка
        history.add(0, track)

        // Ограничиваем размер истории
        if (history.size > MAX_HISTORY_SIZE) {
            history.removeAt(history.size - 1)
        }

        // Сохраняем
        saveHistory(history)
    }

    // Очистить историю
    fun clearHistory() {
        prefs.edit().remove(HISTORY_KEY).apply()
    }

    private fun saveHistory(tracks: List<Track>) {
        val json = gson.toJson(tracks)
        prefs.edit().putString(HISTORY_KEY, json).apply()
    }

}