package com.example.playlistmaker.search.domain

import com.example.playlistmaker.search.data.SearchHistoryRepository
import com.example.playlistmaker.search.domain.model.Track

class SearchHistoryInteractor(private val searchRepository: SearchHistoryRepository) {

    fun addToSearchHistory(track: Track) {
        searchRepository.addToSearchHistory(track)
    }

    fun clearSearchHistory() {
        searchRepository.clearSearchHistory()
    }

    fun getSearchHistory(): List<Track> {
        return searchRepository.getSearchHistory()
    }
}
