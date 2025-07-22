package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.dto.SearchResponse
import com.example.playlistmaker.data.mapper.TrackMapper
import com.example.playlistmaker.data.network.ITunesApi
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.models.Track
import retrofit2.Response

class TrackRepositoryImpl(
    private val apiService: ITunesApi,
    private val mapper: TrackMapper
) : TrackRepository {

    override suspend fun searchTracks(query: String): List<Track> {
        val response: Response<SearchResponse> = apiService.searchTracks(query)

        if (response.isSuccessful) {
            return response.body()?.results?.map { dto ->
                mapper.mapDtoToDomain(dto)
            } ?: emptyList()
        } else {
            throw Exception()
        }
    }
}