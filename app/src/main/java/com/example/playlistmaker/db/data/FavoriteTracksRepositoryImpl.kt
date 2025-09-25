package com.example.playlistmaker.db.data

import com.example.playlistmaker.db.domain.FavoriteTracksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteTracksRepositoryImpl(
    private val appDatabase: AppDatabase
) : FavoriteTracksRepository {

    private val trackDao = appDatabase.trackDao()

    override suspend fun addTrackToFavorites(track: TrackEntity) {
        trackDao.insert(track)
    }

    override suspend fun removeTrackFromFavorites(track: TrackEntity) {
        trackDao.delete(track)
    }

    override fun getAllFavoriteTracks(): Flow<List<TrackEntity>> {
        return trackDao.getAllFavoriteTracks()
            .map { list -> list.sortedByDescending { it.id } }
    }

    override suspend fun isTrackFavorite(trackId: String): Boolean {
        return trackDao.isTrackFavorite(trackId)
    }
}
