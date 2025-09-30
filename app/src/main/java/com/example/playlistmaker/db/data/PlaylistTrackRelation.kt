package com.example.playlistmaker.db.data

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "playlist_track_relations",
    primaryKeys = ["playlist_id", "track_id"]
)
data class PlaylistTrackRelation(
    @ColumnInfo(name = "playlist_id") val playlistId: Long,
    @ColumnInfo(name = "track_id") val trackId: String
)