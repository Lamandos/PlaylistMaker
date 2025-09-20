package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.domain.SearchRepository
import com.example.playlistmaker.search.domain.TrackRepository
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

class SearchRepositoryImpl(
    private val trackRepository: TrackRepository,
    private val searchHistoryRepository: SearchHistoryRepository
) : SearchRepository {

    override suspend fun searchTracks(query: String): Flow<List<Track>> {
        return trackRepository.searchTracks(query)
    }

    override suspend fun addToSearchHistory(track: Track) {
        searchHistoryRepository.addToSearchHistory(track)
    }

    override suspend fun clearSearchHistory() {
        searchHistoryRepository.clearSearchHistory()
    }

    override suspend fun getSearchHistory(): List<Track> {
        return searchHistoryRepository.getSearchHistory()
    }
}
