package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.data.repository.SearchHistory
import com.example.playlistmaker.domain.models.Track

class SearchHistoryInteractor(
    private val repository: SearchHistory
) {
    fun addTrack(track: Track) {
        repository.addToSearchHistory(track)
    }

    fun getHistory(): List<Track> {
        return repository.getSearchHistory()
    }

    fun clear() {
        repository.clearSearchHistory()
    }
}