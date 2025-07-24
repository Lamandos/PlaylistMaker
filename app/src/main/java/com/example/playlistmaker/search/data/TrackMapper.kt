package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.ui.TrackParcelable
import com.example.playlistmaker.search.domain.model.Track

class TrackMapper {
    fun mapDtoToDomain(dto: TrackDto): Track {
        return Track(
            trackName = dto.name ?: "",
            artistName = dto.artist ?: "",
            trackTimeMillis = dto.duration ?: 0,
            artworkUrl100 = dto.artwork ?: "",
            collectionName = dto.album ?: "",
            releaseDate = dto.date ?: "",
            primaryGenreName = dto.genre ?: "",
            country = dto.country ?: "",
            previewUrl = dto.preview ?: ""
        )
    }
    fun mapDomainToParcelable(domain: Track): TrackParcelable {
        return TrackParcelable(
            trackName = domain.trackName,
            artistName = domain.artistName,
            trackTimeMillis = domain.trackTimeMillis,
            artworkUrl100 = domain.artworkUrl100,
            collectionName = domain.collectionName,
            releaseDate = domain.releaseDate,
            primaryGenreName = domain.primaryGenreName,
            country = domain.country,
            previewUrl = domain.previewUrl
        )
    }
    fun mapParcelableToDomain(parcelable: TrackParcelable): Track {
        return Track(
            trackName = parcelable.trackName,
            artistName = parcelable.artistName,
            trackTimeMillis = parcelable.trackTimeMillis,
            artworkUrl100 = parcelable.artworkUrl100,
            collectionName = parcelable.collectionName,
            releaseDate = parcelable.releaseDate,
            primaryGenreName = parcelable.primaryGenreName,
            country = parcelable.country,
            previewUrl = parcelable.previewUrl
        )
    }
    fun mapDtoToParcelable(dto: TrackDto): TrackParcelable {
        return mapDomainToParcelable(mapDtoToDomain(dto))
    }
}