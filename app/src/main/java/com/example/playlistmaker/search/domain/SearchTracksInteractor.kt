package com.example.playlistmaker.search.domain

import com.example.playlistmaker.search.domain.model.Track

class SearchTracksInteractor(
    private val searchRepository: SearchRepository
) {
    suspend fun searchTracks(query: String): Result<List<Track>> {
        val result = searchRepository.searchTracks(query)

        return when {
            result.isSuccess -> {
                Result.success(result.getOrNull() ?: emptyList())
            }
            result.isFailure -> {
                Result.failure(result.exceptionOrNull() ?: Exception("Unknown error"))
            }
            else -> {
                Result.success(emptyList())
            }
        }
    }
}
