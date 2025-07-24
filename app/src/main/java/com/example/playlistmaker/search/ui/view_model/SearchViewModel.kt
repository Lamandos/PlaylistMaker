package com.example.playlistmaker.search.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.search.domain.SearchTracksInteractor
import com.example.playlistmaker.search.domain.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.domain.model.toParcelable
import com.example.playlistmaker.search.ui.TrackParcelable
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchTracksInteractor: SearchTracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    private val _screenState = MutableLiveData(SearchScreenState())
    val screenState: LiveData<SearchScreenState> = _screenState

    private var currentSearchResults: List<TrackParcelable> = emptyList()

    fun searchTracks(query: String) {
        when {
            query.isEmpty() -> {
                _screenState.value = SearchScreenState(
                    userText = query,
                    isFirstSearch = true,
                    historyList = getCurrentHistory()
                )
                return
            }

            else -> {
                _screenState.value = SearchScreenState(
                    isLoading = true,
                    userText = query,
                    historyList = getCurrentHistory()
                )

                viewModelScope.launch {
                    try {
                        searchTracksInteractor.searchTracks(query)
                            .onSuccess { tracks ->
                                currentSearchResults = tracks.map(Track::toParcelable)
                                _screenState.value = SearchScreenState(
                                    trackList = currentSearchResults,
                                    showNoResults = tracks.isEmpty(),
                                    userText = query,
                                    historyList = getCurrentHistory()
                                )
                            }
                            .onFailure {
                                _screenState.value = SearchScreenState(
                                    showNetworkError = true,
                                    userText = query,
                                    historyList = getCurrentHistory()
                                )
                            }
                    } catch (e: Exception) {
                        _screenState.value = SearchScreenState(
                            showNetworkError = true,
                            userText = query,
                            historyList = getCurrentHistory()
                        )
                    }
                }
            }
        }
    }


    fun addToSearchHistory(track: Track) {
        viewModelScope.launch {
            searchHistoryInteractor.addToSearchHistory(track)
            updateHistoryState()
        }
    }

    fun clearSearchHistory() {
        viewModelScope.launch {
            searchHistoryInteractor.clearSearchHistory()
            _screenState.value = _screenState.value?.copy(
                historyList = emptyList(),
                isFirstSearch = true,
                trackList = currentSearchResults
            )
        }
    }

    fun loadSearchHistory() {
        viewModelScope.launch {
            updateHistoryState()
        }
    }

    private fun updateHistoryState() {
        val history = searchHistoryInteractor.getSearchHistory().map(Track::toParcelable)
        _screenState.value = _screenState.value?.copy(
            historyList = history,
            isFirstSearch = history.isEmpty(),
            trackList = currentSearchResults
        )
    }

    fun clearSearchResults() {
        currentSearchResults = emptyList()
        _screenState.value = SearchScreenState(
            trackList = emptyList(),
            showNoResults = false,
            showNetworkError = false,
            isLoading = false,
            historyList = getCurrentHistory()
        )
    }

    private fun getCurrentHistory(): List<TrackParcelable> {
        return _screenState.value?.historyList ?: emptyList()
    }
}
