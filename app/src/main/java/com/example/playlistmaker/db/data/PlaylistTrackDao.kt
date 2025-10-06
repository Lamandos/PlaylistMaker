package com.example.playlistmaker.db.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.playlist_tracks.data.PlaylistTrackWithOrder

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

    @Query("SELECT pt.*, ptr.track_order FROM playlist_tracks pt JOIN playlist_track_relations ptr ON pt.id = ptr.track_id WHERE ptr.playlist_id = :playlistId ORDER BY ptr.track_order ASC")
    suspend fun getTracksByPlaylistId(playlistId: Long): List<PlaylistTrackWithOrder>

    @Query("SELECT MAX(track_order) FROM playlist_track_relations WHERE playlist_id = :playlistId")
    suspend fun getMaxTrackOrder(playlistId: Long): Long?

    @Query("UPDATE playlist_track_relations SET track_order = :newOrder WHERE playlist_id = :playlistId AND track_id = :trackId")
    suspend fun updateTrackOrder(playlistId: Long, trackId: String, newOrder: Long)

    @Query("DELETE FROM playlist_track_relations WHERE playlist_id = :playlistId AND track_id = :trackId")
    suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: String)

    @Query("DELETE FROM playlist_tracks WHERE id = :trackId")
    suspend fun deleteTrackById(trackId: String)

    @Query("DELETE FROM playlist_track_relations WHERE playlist_Id = :playlistId")
    suspend fun removeTracksFromPlaylist(playlistId: Long)

    @Delete
    suspend fun delete(playlist: PlaylistEntity)

    @Query("DELETE FROM playlists WHERE id = :playlistId")
    suspend fun deleteById(playlistId: Long)
}
