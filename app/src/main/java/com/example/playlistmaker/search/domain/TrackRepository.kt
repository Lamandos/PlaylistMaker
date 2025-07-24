package com.example.playlistmaker.search.domain

import com.example.playlistmaker.search.domain.model.Track

interface TrackRepository {
    suspend fun searchTracks(query: String): List<Track>
}