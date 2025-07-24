package com.example.playlistmaker.search.data

data class SearchResponse(
    val resultCount: Int,
    val results: List<TrackDto>
)