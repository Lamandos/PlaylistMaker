package com.example.playlistmaker.db.domain

import com.example.playlistmaker.db.data.toTrack
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.domain.model.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteTracksInteractorImpl(
    private val repository: FavoriteTracksRepository
) : FavoriteTracksInteractor {

    override suspend fun addTrack(track: Track) {
        repository.addTrackToFavorites(track.toEntity())
    }

    override suspend fun removeTrack(track: Track) {
        repository.removeTrackFromFavorites(track.toEntity())
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return repository.getAllFavoriteTracks().map { trackEntities ->
            trackEntities.map { it.toTrack() }
        }
    }

    override suspend fun isTrackFavorite(trackId: String): Boolean {
        return repository.isTrackFavorite(trackId)
    }
}
