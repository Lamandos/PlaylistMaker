package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface TrackRepository {
    suspend fun searchTracks(query: String): List<Track>
}