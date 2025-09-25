package com.example.playlistmaker.db.domain

import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksInteractor {
    suspend fun addTrack(track: Track)
    suspend fun removeTrack(track: Track)
    fun getFavoriteTracks(): Flow<List<Track>>
    suspend fun isTrackFavorite(trackId: String): Boolean
}