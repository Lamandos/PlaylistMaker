package com.example.playlistmaker.playlist.ui.model

data class PlaylistUi(
    val id: Long,
    val name: String,
    val description: String?,
    val coverImagePath: String?,
    val trackCount: Int
)