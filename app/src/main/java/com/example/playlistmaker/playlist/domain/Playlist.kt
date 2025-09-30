package com.example.playlistmaker.playlist.domain

data class Playlist(
    val id: Long,
    val name: String,
    val description: String?,
    val coverImagePath: String?,
    val trackCount: Int
)