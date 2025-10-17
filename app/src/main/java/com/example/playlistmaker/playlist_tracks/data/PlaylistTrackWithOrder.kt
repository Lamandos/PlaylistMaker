package com.example.playlistmaker.playlist_tracks.data

data class PlaylistTrackWithOrder(
    val id: String,
    val trackName: String,
    val artistName: String,
    val artworkUrl: String,
    val albumName: String?,
    val releaseDate: String?,
    val genre: String,
    val country: String,
    val duration: Long,
    val previewUrl: String,
    val dateAdded: Long,
    val track_order: Long
)