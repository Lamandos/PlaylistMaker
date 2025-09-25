package com.example.playlistmaker.db.domain

import com.example.playlistmaker.db.data.TrackEntity
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksRepository {

    suspend fun addTrackToFavorites(track: TrackEntity)

    suspend fun removeTrackFromFavorites(track: TrackEntity)

    fun getAllFavoriteTracks(): Flow<List<TrackEntity>>
    suspend fun isTrackFavorite(trackId: String): Boolean
}
