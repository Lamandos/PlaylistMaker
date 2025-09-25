package com.example.playlistmaker.search.data

import com.example.playlistmaker.db.data.AppDatabase
import com.example.playlistmaker.search.domain.TrackRepository
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class TrackRepositoryImpl(
    private val apiService: ITunesApi,
    private val mapper: TrackMapper,
    private val appDatabase: AppDatabase,
) : TrackRepository {

    override suspend fun searchTracks(query: String): Flow<List<Track>> = flow {
        val response: Response<SearchResponse> = apiService.searchTracks(query)

        if (response.isSuccessful) {
            val tracks = response.body()?.results?.map { dto ->
                mapper.mapDtoToDomain(dto)
            } ?: emptyList()

            val favoriteIds = appDatabase.trackDao().getAllFavoriteTrackIds().first()
            tracks.forEach { track ->
                if (favoriteIds.contains(track.id)) {
                    track.isFavorite = true
                }
            }

            emit(tracks)
        } else {
            emit(emptyList())
        }
    }
}