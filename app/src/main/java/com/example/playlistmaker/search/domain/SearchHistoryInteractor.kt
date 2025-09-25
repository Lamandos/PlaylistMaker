package com.example.playlistmaker.search.domain

import com.example.playlistmaker.db.data.AppDatabase
import com.example.playlistmaker.search.data.SearchHistoryRepository
import com.example.playlistmaker.search.domain.model.Track

class SearchHistoryInteractor(
    private val searchRepository: SearchHistoryRepository,
    private val appDatabase: AppDatabase
) {

    suspend fun addToSearchHistory(track: Track) {
        searchRepository.addToSearchHistory(track, appDatabase)
    }

    suspend fun clearSearchHistory() {
        searchRepository.clearSearchHistory()
    }

    suspend fun getSearchHistory(): List<Track> {
        return searchRepository.getSearchHistory(appDatabase)
    }
}

