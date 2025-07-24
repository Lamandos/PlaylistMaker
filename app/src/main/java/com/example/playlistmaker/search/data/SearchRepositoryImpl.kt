package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.domain.SearchRepository
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.domain.TrackRepository

class SearchRepositoryImpl(
    private val trackRepository: TrackRepository,
    private val searchHistoryRepository: SearchHistoryRepository
) : SearchRepository {

    override suspend fun searchTracks(query: String): Result<List<Track>> {
        return try {
            val tracks = trackRepository.searchTracks(query)
            Result.success(tracks)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override fun addToSearchHistory(track: Track) {
        searchHistoryRepository.addToSearchHistory(track)
    }
    override fun clearSearchHistory() {
        searchHistoryRepository.clearSearchHistory()
    }
    override fun getSearchHistory(): List<Track> {
        return searchHistoryRepository.getSearchHistory()
    }
}
