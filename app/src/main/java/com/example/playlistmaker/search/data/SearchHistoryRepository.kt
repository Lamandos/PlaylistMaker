package com.example.playlistmaker.search.data

import android.content.SharedPreferences
import com.example.playlistmaker.db.data.AppDatabase
import com.example.playlistmaker.search.domain.model.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class SearchHistoryRepository(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) {

    private val key = "search_history"
    private val maxHistorySize = 10

    suspend fun getSearchHistory(appDatabase: AppDatabase): List<Track> = withContext(Dispatchers.IO) {
        val json = sharedPreferences.getString(key, null) ?: return@withContext emptyList()
        val history: List<Track> = try {
            val type = object : TypeToken<List<Track>>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }

        val favoriteIds = appDatabase.trackDao().getAllFavoriteTrackIds().first()

        history.forEach { track ->
            if (favoriteIds.contains(track.id)) {
                track.isFavorite = true
            }
        }

        return@withContext history
    }


    suspend fun saveSearchHistory(history: List<Track>) = withContext(Dispatchers.IO) {
        val historyJson = gson.toJson(history)
        sharedPreferences.edit().putString(key, historyJson).apply()
    }

    suspend fun clearSearchHistory() = withContext(Dispatchers.IO) {
        sharedPreferences.edit().remove(key).apply()
    }

    suspend fun addToSearchHistory(track: Track, appDatabase: AppDatabase) = withContext(Dispatchers.IO) {
        val currentHistory = getSearchHistory(appDatabase).toMutableList()
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

