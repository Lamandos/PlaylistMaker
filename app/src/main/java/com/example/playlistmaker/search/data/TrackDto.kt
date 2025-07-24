package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.ui.TrackParcelable
import com.google.gson.annotations.SerializedName

data class TrackDto(
    @SerializedName("trackName") val name: String,
    @SerializedName("artistName") val artist: String,
    @SerializedName("trackTimeMillis") val duration: Long,
    @SerializedName("artworkUrl100") val artwork: String,
    @SerializedName("collectionName") val album: String?,
    @SerializedName("primaryGenreName") val genre: String,
    @SerializedName("previewUrl") val preview: String,
    @SerializedName("country") val country: String,
    @SerializedName("releaseDate") val date: String?
)
    fun TrackDto.toDomain(): Track {
        return Track(
        trackName = name,
        artistName = artist,
        trackTimeMillis = duration,
        artworkUrl100 = artwork,
        collectionName = album,
        primaryGenreName = genre,
        previewUrl = preview,
        country = country,
        releaseDate = date
    )
}
fun TrackDto.toParcelable(): TrackParcelable {
    return TrackParcelable(
        trackName = this.name,
        artistName = this.artist,
        trackTimeMillis = this.duration,
        artworkUrl100 = this.artwork,
        collectionName = this.album,
        releaseDate = this.date,
        primaryGenreName = this.genre,
        country = this.country,
        previewUrl = this.preview
    )
}