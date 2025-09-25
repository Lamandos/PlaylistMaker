package com.example.playlistmaker.media.ui.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.db.domain.FavoriteTracksInteractor
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.launch

data class FavoriteScreenState(
    val isEmpty: Boolean = false,
    val favoriteTracks: List<Track> = emptyList()
)

class FavoritesViewModel(
    private val favoriteInteractor: FavoriteTracksInteractor
) : ViewModel() {

    private val _screenState = MutableLiveData<FavoriteScreenState>()
    val screenState: LiveData<FavoriteScreenState> = _screenState

    init {
        fetchFavoriteTracks()
    }

    private fun fetchFavoriteTracks() {
        viewModelScope.launch {
            favoriteInteractor.getFavoriteTracks().collect { tracks ->
                val sortedTracks = tracks.sortedByDescending { it.dateAdded }

                val state = if (sortedTracks.isEmpty()) {
                    FavoriteScreenState(isEmpty = true)
                } else {
                    FavoriteScreenState(isEmpty = false, favoriteTracks = sortedTracks)
                }
                _screenState.postValue(state)
            }
        }
    }
}
