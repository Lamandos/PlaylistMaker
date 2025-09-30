package com.example.playlistmaker.playlist.domain

import com.example.playlistmaker.db.data.PlaylistEntity
import com.example.playlistmaker.playlist.ui.model.PlaylistUi

fun PlaylistEntity.toDomain(): Playlist = Playlist(
    id = id,
    name = name,
    description = description,
    coverImagePath = coverImagePath,
    trackCount = trackCount
)

fun Playlist.toUi(): PlaylistUi = PlaylistUi(
    id = id,
    name = name,
    description = description,
    coverImagePath = coverImagePath,
    trackCount = trackCount
)

fun Playlist.toEntity(): PlaylistEntity {
    return PlaylistEntity(
        id = this.id,
        name = this.name,
        description = this.description,
        coverImagePath = this.coverImagePath,
        trackCount = this.trackCount
    )
}