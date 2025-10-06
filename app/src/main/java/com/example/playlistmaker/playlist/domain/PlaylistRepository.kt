package com.example.playlistmaker.playlist.domain

import com.example.playlistmaker.db.data.PlaylistDao
import com.example.playlistmaker.db.data.PlaylistEntity
import com.example.playlistmaker.db.data.PlaylistTrackDao
import com.example.playlistmaker.db.data.PlaylistTrackEntity
import com.example.playlistmaker.db.data.PlaylistTrackRelation
import com.example.playlistmaker.search.domain.model.Track

class PlaylistRepository(
    private val playlistDao: PlaylistDao,
    private val playlistTrackDao: PlaylistTrackDao
) {

    suspend fun addPlaylist(playlistEntity: PlaylistEntity) {
        playlistDao.insert(playlistEntity)
    }

    suspend fun updatePlaylist(playlistEntity: PlaylistEntity) {
        playlistDao.update(playlistEntity)
    }

    suspend fun getAllPlaylists(): List<Playlist> {
        return playlistDao.getAllPlaylists().map { it.toDomain() }
    }

    suspend fun getPlaylistById(id: Long): Playlist? {
        return playlistDao.getPlaylistById(id)?.toDomain()
    }

    suspend fun addTrackToPlaylist(track: Track, playlist: PlaylistEntity): Boolean {
        val existingRelation = playlistTrackDao.getRelation(playlist.id, track.id)
        if (existingRelation != null) {
            return false
        }

        val maxTrackOrder = playlistTrackDao.getMaxTrackOrder(playlist.id) ?: 0
        val newTrackOrder = maxTrackOrder + 1

        val existingTrack = playlistTrackDao.getTrackById(track.id)
        if (existingTrack == null) {
            val playlistTrackEntity = PlaylistTrackEntity(
                id = track.id,
                trackName = track.trackName,
                artistName = track.artistName,
                artworkUrl = track.artworkUrl100,
                albumName = track.collectionName,
                releaseDate = track.releaseDate,
                genre = track.primaryGenreName,
                country = track.country,
                duration = track.trackTimeMillis,
                previewUrl = track.previewUrl,
                dateAdded = System.currentTimeMillis()
            )
            playlistTrackDao.insertTrack(playlistTrackEntity)
        }

        playlistTrackDao.insertRelation(PlaylistTrackRelation(playlist.id, track.id, newTrackOrder))

        val trackCount = playlistTrackDao.getTrackCount(playlist.id)
        val updatedPlaylist = playlist.copy(trackCount = trackCount)
        playlistDao.update(updatedPlaylist)

        return true
    }

    suspend fun getPlaylistTracks(playlistId: Long): List<Track> {
        return playlistTrackDao.getTracksByPlaylistId(playlistId).map { entity ->
            Track(
                id = entity.id,
                trackName = entity.trackName,
                artistName = entity.artistName,
                trackTimeMillis = entity.duration,
                artworkUrl100 = entity.artworkUrl,
                collectionName = entity.albumName,
                releaseDate = entity.releaseDate,
                primaryGenreName = entity.genre,
                country = entity.country,
                previewUrl = entity.previewUrl,
                trackOrder = entity.track_order
            )
        }
    }


    suspend fun checkAndDeleteTrackIfUnused(trackId: String) {
        val playlists = playlistDao.getAllPlaylists()

        val isTrackUsedInAnyPlaylist = playlists.any { playlist ->
            playlistTrackDao.getRelation(playlist.id, trackId) != null
        }

        if (!isTrackUsedInAnyPlaylist) {
            playlistTrackDao.deleteTrackById(trackId)
        }
    }

    suspend fun deleteTrackFromPlaylist(trackId: String, playlistId: Long) {

        playlistTrackDao.removeTrackFromPlaylist(playlistId, trackId)

        val trackCount = playlistTrackDao.getTrackCount(playlistId)
        val playlist = playlistDao.getPlaylistById(playlistId) ?: return
        val updatedPlaylist = playlist.copy(trackCount = trackCount)
        playlistDao.update(updatedPlaylist)
    }

    suspend fun deletePlaylist(id: Long) {
        val playlist = playlistDao.getPlaylistById(id) ?: return

        val playlistTracks = playlistTrackDao.getTracksByPlaylistId(id)

        playlistTrackDao.removeTracksFromPlaylist(id)

        playlistTracks.forEach { track ->
            checkAndDeleteTrackIfUnused(track.id)
        }

        playlistDao.delete(playlist)
    }
}
