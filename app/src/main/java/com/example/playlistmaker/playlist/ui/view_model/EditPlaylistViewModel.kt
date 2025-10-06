package com.example.playlistmaker.playlist.ui.view_model

import androidx.lifecycle.viewModelScope
import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.example.playlistmaker.db.data.PlaylistEntity
import com.example.playlistmaker.playlist.domain.PlaylistInteractor
import com.example.playlistmaker.playlist.domain.toEntity
import kotlinx.coroutines.launch

class EditPlaylistViewModel(
    application: Application,
    override val playlistInteractor: PlaylistInteractor
) : PlaylistViewModel(application, playlistInteractor) {

    private val _playlist = MutableLiveData<PlaylistEntity?>()
    val playlist: MutableLiveData<PlaylistEntity?> = _playlist

    fun loadPlaylist(playlistId: Long) {
        viewModelScope.launch {

                val playlist = playlistInteractor.getPlaylistById(playlistId)
                _playlist.postValue(playlist?.toEntity())

        }
    }

    fun updatePlaylist(playlist: PlaylistEntity) {
        viewModelScope.launch {

                playlistInteractor.updatePlaylist(playlist)

        }
    }
}
