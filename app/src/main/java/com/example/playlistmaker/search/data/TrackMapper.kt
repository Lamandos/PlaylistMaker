package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.ui.TrackParcelable
import com.example.playlistmaker.search.domain.model.Track

class TrackMapper {

    private fun generateId(dto: TrackDto): String {
        return "${dto.name}_${dto.artist}_${dto.album ?: ""}".hashCode().toString()
    }

    fun mapDtoToDomain(dto: TrackDto): Track {
        val id = generateId(dto)
        return Track(
            id = id,
            trackName = dto.name ?: "",
            artistName = dto.artist ?: "",
            trackTimeMillis = dto.duration ?: 0,
            artworkUrl100 = dto.artwork ?: "",
            collectionName = dto.album ?: "",
            releaseDate = dto.date ?: "",
            primaryGenreName = dto.genre ?: "",
            country = dto.country ?: "",
            previewUrl = dto.preview ?: "",
            isFavorite = false,
            dateAdded = System.currentTimeMillis()
        )
    }

    fun mapDomainToParcelable(domain: Track): TrackParcelable {
        return TrackParcelable(
            id = domain.id,
            trackName = domain.trackName,
            artistName = domain.artistName,
            trackTimeMillis = domain.trackTimeMillis,
            artworkUrl100 = domain.artworkUrl100,
            collectionName = domain.collectionName,
            releaseDate = domain.releaseDate,
            primaryGenreName = domain.primaryGenreName,
            country = domain.country,
            previewUrl = domain.previewUrl,
            isFavorite = domain.isFavorite
        )
    }

    fun mapParcelableToDomain(parcelable: TrackParcelable): Track {
        return Track(
            id = parcelable.id,
            trackName = parcelable.trackName,
            artistName = parcelable.artistName,
            trackTimeMillis = parcelable.trackTimeMillis,
            artworkUrl100 = parcelable.artworkUrl100,
            collectionName = parcelable.collectionName,
            releaseDate = parcelable.releaseDate,
            primaryGenreName = parcelable.primaryGenreName,
            country = parcelable.country,
            previewUrl = parcelable.previewUrl,
            isFavorite = false
        )
    }

    fun mapDtoToParcelable(dto: TrackDto): TrackParcelable {
        return mapDomainToParcelable(mapDtoToDomain(dto))
    }
}
