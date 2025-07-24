package com.example.playlistmaker.search.ui.view_model

import com.example.playlistmaker.search.ui.TrackParcelable

data class SearchScreenState(
    val isLoading: Boolean = false,
    val showNetworkError: Boolean = false,
    val showNoResults: Boolean = false,
    val trackList: List<TrackParcelable> = emptyList(),
    val historyList: List<TrackParcelable> = emptyList(),
    val userText: String = "",
    val isFirstSearch: Boolean = false
)
