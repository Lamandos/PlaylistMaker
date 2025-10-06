package com.example.playlistmaker.db.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE

@Entity(
    tableName = "playlist_track_relations",
    primaryKeys = ["playlist_id", "track_id"],
    foreignKeys = [
        ForeignKey(
            entity = PlaylistEntity::class,
            parentColumns = ["id"],
            childColumns = ["playlist_id"],
            onDelete = CASCADE
        ),
        ForeignKey(
            entity = PlaylistTrackEntity::class,
            parentColumns = ["id"],
            childColumns = ["track_id"],
            onDelete = CASCADE
        )
    ]
)
data class PlaylistTrackRelation(
    val playlist_id: Long,
    val track_id: String,
    val track_order: Long
)