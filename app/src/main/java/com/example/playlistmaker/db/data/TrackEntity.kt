package com.example.playlistmaker.db.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.playlistmaker.search.domain.model.Track

@Entity(tableName = "favorite_tracks")
data class TrackEntity(
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
    val dateAdded: Long,
    val isFavorite: Boolean = false
)
fun TrackEntity.toTrack(): Track {
    return Track(
        trackName = trackName,
        artistName = artistName,
        trackTimeMillis = duration,
        artworkUrl100 = artworkUrl,
        collectionName = albumName,
        releaseDate = releaseDate,
        primaryGenreName = genre,
        country = country,
        previewUrl = previewUrl,
        id = id,
        dateAdded = dateAdded,
        isFavorite = isFavorite
    )
}
