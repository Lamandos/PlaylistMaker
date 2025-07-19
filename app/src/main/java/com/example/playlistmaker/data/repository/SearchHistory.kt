package com.example.playlistmaker.data.repository

import android.content.SharedPreferences
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(private val sharedPreferences: SharedPreferences) {
    private val gson = Gson()
    private val key = "search_history"
    private val maxHistorySize = 10
    fun getSearchHistory(): List<Track> {
        val json = sharedPreferences.getString(key, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<Track>>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveSearchHistory(history: List<Track>) {
        val historyJson = gson.toJson(history)
        sharedPreferences.edit().putString(key, historyJson).apply()
    }

    fun clearSearchHistory() {
        sharedPreferences.edit().remove(key).apply()
    }

    fun addToSearchHistory(track: Track) {
        val currentHistory = getSearchHistory().toMutableList()
        currentHistory.removeAll {
            it.trackName == track.trackName &&
                    it.artistName == track.artistName &&
                    it.trackTimeMillis == track.trackTimeMillis
        }

        currentHistory.add(0, track)

        if (currentHistory.size > maxHistorySize) {
            currentHistory.removeAt(currentHistory.lastIndex)
        }
        saveSearchHistory(currentHistory)
    }
}