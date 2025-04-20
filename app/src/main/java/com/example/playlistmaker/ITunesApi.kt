package com.example.playlistmaker
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Response

interface ITunesApi {
    @GET("search?entity=song")
    suspend fun searchTracks(@Query("term") query: String): Response<SearchResponse>
}