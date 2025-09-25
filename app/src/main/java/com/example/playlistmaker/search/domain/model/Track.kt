package com.example.playlistmaker.search.domain.model

import com.example.playlistmaker.db.data.TrackEntity
import com.example.playlistmaker.search.ui.TrackParcelable


data class Track(
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String,
    val id: String,
    val dateAdded: Long = System.currentTimeMillis(),
    var isFavorite: Boolean = false
)
fun Track.toParcelable(): TrackParcelable {
    return TrackParcelable(
        trackName = trackName,
        artistName = artistName,
        trackTimeMillis = trackTimeMillis,
        artworkUrl100 = artworkUrl100,
        collectionName = collectionName,
        releaseDate = releaseDate,
        primaryGenreName = primaryGenreName,
        country = country,
        previewUrl = previewUrl,
        id = id,
        isFavorite = isFavorite
    )
}

fun Track.toEntity(): TrackEntity {
    return TrackEntity(
        id = id,
        artworkUrl = artworkUrl100,
        trackName = trackName,
        artistName = artistName,
        albumName = collectionName,
        releaseDate = releaseDate,
        genre = primaryGenreName,
        country = country,
        duration = trackTimeMillis,
        previewUrl = previewUrl,
        dateAdded = dateAdded,
        isFavorite = isFavorite
    )
}
fun TrackParcelable.toTrack(): Track {
    return Track(
        trackName = trackName,
        artistName = artistName,
        trackTimeMillis = trackTimeMillis,
        artworkUrl100 = artworkUrl100,
        collectionName = collectionName,
        releaseDate = releaseDate,
        primaryGenreName = primaryGenreName,
        country = country,
        previewUrl = previewUrl,
        id = id,
        isFavorite = isFavorite
    )
}