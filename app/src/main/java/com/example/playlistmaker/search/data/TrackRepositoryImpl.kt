package com.example.playlistmaker.search.data


import com.example.playlistmaker.search.domain.TrackRepository
import com.example.playlistmaker.search.domain.model.Track
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