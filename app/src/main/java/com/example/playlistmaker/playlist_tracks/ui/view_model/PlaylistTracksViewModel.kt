package com.example.playlistmaker.playlist_tracks.ui.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.playlist.domain.Playlist
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.playlist.domain.PlaylistRepository
import kotlinx.coroutines.launch

class PlaylistTracksViewModel(
    application: Application,
    private val playlistRepository: PlaylistRepository
) : AndroidViewModel(application) {

    private val _playlist = MutableLiveData<Playlist?>()
    val playlist: LiveData<Playlist?> get() = _playlist

    private val _tracks = MutableLiveData<List<Track>>()
    val tracks: LiveData<List<Track>> get() = _tracks

    fun loadPlaylist(playlistId: Long) {
        viewModelScope.launch {
            try {
                _playlist.postValue(null)
                val pl = playlistRepository.getPlaylistById(playlistId)
                _playlist.postValue(pl)

                if (pl != null) {
                    val trackList = playlistRepository.getPlaylistTracks(playlistId)
                    val sortedTrackList = trackList.sortedByDescending { it.trackOrder }
                    _tracks.postValue(sortedTrackList)
                } else {
                    _tracks.postValue(emptyList())
                }
            } catch (e: Exception) {
                _tracks.postValue(emptyList())
            }
        }
    }

    private val _deleteStatus = MutableLiveData<Boolean>()
    val deleteStatus: LiveData<Boolean> get() = _deleteStatus

    fun deletePlaylist() {
        val pl = _playlist.value ?: return
        viewModelScope.launch {
            try {
                playlistRepository.deletePlaylist(pl.id)
                _deleteStatus.postValue(true)
                _playlist.postValue(null)
                _tracks.postValue(emptyList())
            } catch (e: Exception) {
                _deleteStatus.postValue(false)
                e.printStackTrace()
            }
        }
    }

    fun buildShareText(): String? {
        val pl = _playlist.value ?: return null
        val tracksList = _tracks.value ?: return null

        if (tracksList.isEmpty()) return null

        return buildString {
            appendLine(pl.name)
            if (!pl.description.isNullOrEmpty()) appendLine(pl.description)
            appendLine("${tracksList.size} трек${getTrackWordForm(tracksList.size)}")
            tracksList.forEachIndexed { index, track ->
                appendLine("${index + 1}. ${track.artistName} - ${track.trackName} (${formatDuration(track.trackTimeMillis)})")
            }
        }
    }

    private fun getTrackWordForm(count: Int): String {
        val rem100 = count % 100
        val rem10 = count % 10
        return when {
            rem100 in 11..14 -> "ов"
            rem10 == 1 -> ""
            rem10 in 2..4 -> "а"
            else -> "ов"
        }
    }

    fun deleteTrackFromPlaylist(trackId: String) {
        val pl = _playlist.value ?: return
        val playlistId = pl.id

        viewModelScope.launch {
            playlistRepository.deleteTrackFromPlaylist(trackId, playlistId)
            playlistRepository.checkAndDeleteTrackIfUnused(trackId)

            loadPlaylist(playlistId)
        }
    }

    private fun formatDuration(durationMs: Long): String {
        val minutes = durationMs / 60000
        val seconds = (durationMs % 60000) / 1000
        return "%d:%02d".format(minutes, seconds)
    }
}
