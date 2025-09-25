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