package com.example.playlistmaker.search.domain

import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    suspend fun searchTracks(query: String): Flow<List<Track>>
    suspend fun addToSearchHistory(track: Track)
    suspend fun clearSearchHistory()
    suspend fun getSearchHistory(): List<Track>
}
