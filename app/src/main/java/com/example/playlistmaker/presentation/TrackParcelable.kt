package com.example.playlistmaker.presentation
import android.os.Parcelable
import com.example.playlistmaker.domain.models.Track
import kotlinx.parcelize.Parcelize

@Parcelize
data class TrackParcelable(
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String?
) : Parcelable


fun TrackParcelable.toDomain(): Track {
    return Track(
        trackName = this.trackName,
        artistName = this.artistName,
        trackTimeMillis = this.trackTimeMillis,
        artworkUrl100 = this.artworkUrl100,
        collectionName = this.collectionName,
        releaseDate = this.releaseDate,
        primaryGenreName = this.primaryGenreName,
        country = this.country,
        previewUrl = this.previewUrl

    )
}