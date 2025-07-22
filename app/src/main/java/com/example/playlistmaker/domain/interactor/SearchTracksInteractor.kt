package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.models.Track

class SearchTracksInteractor(
    private val trackRepository: TrackRepository
) {
    suspend fun searchTracks(query: String): List<Track> {
        return trackRepository.searchTracks(query)
    }
}