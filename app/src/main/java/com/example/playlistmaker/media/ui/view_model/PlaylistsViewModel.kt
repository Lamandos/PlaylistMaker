package com.example.playlistmaker.media.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.playlist.domain.PlaylistInteractor
import com.example.playlistmaker.playlist.ui.model.PlaylistUi
import kotlinx.coroutines.launch
import com.example.playlistmaker.playlist.domain.toUi

class PlaylistsViewModel(private val interactor: PlaylistInteractor) : ViewModel() {

    private val _playlists = MutableLiveData<List<PlaylistUi>>()
    val playlists: LiveData<List<PlaylistUi>> = _playlists

    fun loadPlaylists() {
        viewModelScope.launch {
            val list = interactor.getAllPlaylists().map { it.toUi() }
            _playlists.postValue(list)
        }
    }
}
