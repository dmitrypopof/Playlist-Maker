package com.example.playlistmaker.feature.search.data.repository

import android.content.SharedPreferences
import com.example.playlistmaker.feature.favorite.data.db.AppDatabase
import com.example.playlistmaker.feature.search.domain.model.Track
import com.example.playlistmaker.feature.search.domain.repository.SearchHistoryRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistoryRepositoryImpl(
    private val prefs: SharedPreferences,
    private val gson: Gson,
    private val appDatabase: AppDatabase
) : SearchHistoryRepository {
    private val maxSize = 10

    override suspend fun getHistory(): List<Track> {
        val json = prefs.getString("history_json", null)
        val history: List<Track> = if (json != null) {
            val type = object : TypeToken<List<Track>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } else {
            emptyList()
        }
        markFavoriteTracks(history)
        return history
    }

    override fun addTrack(track: Track) {
        val json = prefs.getString("history_json", null)
        val currentHistory: MutableList<Track> = if (json != null) {
            val type = object : TypeToken<List<Track>>() {}.type
            val list: List<Track> = gson.fromJson(json, type) ?: emptyList()
            list.toMutableList()
        } else {
            mutableListOf()
        }
        currentHistory.removeAll { it.trackName == track.trackName && it.artistName == track.artistName }
        currentHistory.add(0, track)
        if (currentHistory.size > maxSize) {
            currentHistory.removeAt(currentHistory.size - 1)
        }
        saveHistory(currentHistory)
    }

    override fun clearHistory() {
        saveHistory(emptyList())
    }

    private fun saveHistory(history: List<Track>) {
        val json = gson.toJson(history)
        prefs.edit().putString("history_json", json).apply()
    }

    private suspend fun markFavoriteTracks(tracks: List<Track>) {
        val favoriteIds = appDatabase.favoriteTrackDao().getFavoriteTrackIds()
        tracks.forEach { track ->
            track.isFavorite = favoriteIds.contains(track.trackId)
        }
    }
}