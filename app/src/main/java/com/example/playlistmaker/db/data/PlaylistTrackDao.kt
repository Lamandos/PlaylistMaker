package com.example.playlistmaker.db.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PlaylistTrackDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(track: PlaylistTrackEntity)

    @Query("SELECT * FROM playlist_tracks WHERE id = :trackId")
    suspend fun getTrackById(trackId: String): PlaylistTrackEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRelation(relation: PlaylistTrackRelation)

    @Query("SELECT * FROM playlist_track_relations WHERE playlist_id = :playlistId AND track_id = :trackId")
    suspend fun getRelation(playlistId: Long, trackId: String): PlaylistTrackRelation?

    @Query("SELECT COUNT(*) FROM playlist_track_relations WHERE playlist_id = :playlistId")
    suspend fun getTrackCount(playlistId: Long): Int

    @Query("SELECT pt.* FROM playlist_tracks pt JOIN playlist_track_relations ptr ON pt.id = ptr.track_id WHERE ptr.playlist_id = :playlistId")
    suspend fun getTracksByPlaylistId(playlistId: Long): List<PlaylistTrackEntity>
}