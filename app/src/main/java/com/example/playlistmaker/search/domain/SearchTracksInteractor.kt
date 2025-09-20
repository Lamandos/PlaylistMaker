package com.example.playlistmaker.search.domain

import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchTracksInteractor(
    private val searchRepository: SearchRepository
) {
    suspend fun searchTracks(query: String): Flow<List<Track>> {
        return searchRepository.searchTracks(query)
    }
}
