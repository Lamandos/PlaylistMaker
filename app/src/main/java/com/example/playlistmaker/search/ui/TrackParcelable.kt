package com.example.playlistmaker.search.ui
import android.os.Parcelable
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.parcelize.Parcelize

@Parcelize
data class TrackParcelable(
    val id: String,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String,
    val isFavorite: Boolean
) : Parcelable


fun TrackParcelable.toDomain(): Track {
    return Track(
        id = this.id,
        trackName = this.trackName,
        artistName = this.artistName,
        trackTimeMillis = this.trackTimeMillis,
        artworkUrl100 = this.artworkUrl100,
        collectionName = this.collectionName,
        releaseDate = this.releaseDate,
        primaryGenreName = this.primaryGenreName,
        country = this.country,
        previewUrl = this.previewUrl,
        isFavorite = this.isFavorite
    )
}