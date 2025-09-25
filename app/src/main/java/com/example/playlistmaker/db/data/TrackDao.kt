package com.example.playlistmaker.db.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(track: TrackEntity)

    @Delete
    suspend fun delete(track: TrackEntity)

    @Query("SELECT * FROM favorite_tracks")
    fun getAllFavoriteTracks(): Flow<List<TrackEntity>>

    @Query("SELECT id FROM favorite_tracks")
    fun getAllFavoriteTrackIds(): Flow<List<String>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_tracks WHERE id = :trackId)")
    suspend fun isTrackFavorite(trackId: String): Boolean
}
