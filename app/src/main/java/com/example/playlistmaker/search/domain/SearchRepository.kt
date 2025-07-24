package com.example.playlistmaker.search.domain

import com.example.playlistmaker.search.domain.model.Track

interface SearchRepository {
    suspend fun searchTracks(query: String): Result<List<Track>>
    fun addToSearchHistory(track: Track)
    fun clearSearchHistory()
    fun getSearchHistory(): List<Track>
}

