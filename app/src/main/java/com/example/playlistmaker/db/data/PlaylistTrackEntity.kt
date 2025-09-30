package com.example.playlistmaker.db.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_tracks")
data class PlaylistTrackEntity(
    @PrimaryKey
    val id: String,
    val artworkUrl: String,
    val trackName: String,
    val artistName: String,
    val albumName: String?,
    val releaseDate: String?,
    val genre: String,
    val country: String,
    val duration: Long,
    val previewUrl: String,
    val dateAdded: Long
)
