package com.example.playlistmaker.playlist.domain

import com.example.playlistmaker.db.data.PlaylistEntity



class PlaylistInteractor(private val playlistRepository: PlaylistRepository) {

    suspend fun createPlaylist(playlist: PlaylistEntity) {
        playlistRepository.addPlaylist(playlist)
    }

    suspend fun updatePlaylist(playlist: PlaylistEntity) {
        playlistRepository.updatePlaylist(playlist)
    }

    suspend fun getAllPlaylists(): List<Playlist> {
        return playlistRepository.getAllPlaylists()
    }

    suspend fun getPlaylistById(id: Long): Playlist? {
        return playlistRepository.getPlaylistById(id)
    }
}