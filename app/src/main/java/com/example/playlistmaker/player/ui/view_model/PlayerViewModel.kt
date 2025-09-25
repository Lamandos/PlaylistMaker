package com.example.playlistmaker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.db.domain.FavoriteTracksInteractor
import com.example.playlistmaker.player.PlayerState
import com.example.playlistmaker.player.domain.PlayerInteractor
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.launch

data class PlayerScreenState(
    val playerState: PlayerState = PlayerState.Default,
    val currentTime: String = "00:00",
    val trackTitle: String = "",
    val trackArtist: String = "",
    val isFavorite: Boolean = false
)

class PlayerViewModel(
    private val interactor: PlayerInteractor,
    private val favoriteInteractor: FavoriteTracksInteractor
) : ViewModel() {

    private val _screenState = MutableLiveData<PlayerScreenState>()
    val screenState: LiveData<PlayerScreenState> = _screenState

    private var currentTrack: Track? = null
    private var currentUrl: String = ""
    fun preparePlayer(url: String, title: String, artist: String, track: Track) {
        currentUrl = url
        currentTrack = track

        viewModelScope.launch {
            val isFavorite = favoriteInteractor.isTrackFavorite(track.id)
            currentTrack = track.copy(isFavorite = isFavorite)

            _screenState.postValue(
                PlayerScreenState(
                    playerState = PlayerState.Preparing,
                    trackTitle = title,
                    trackArtist = artist,
                    currentTime = "00:00",
                    isFavorite = isFavorite
                )
            )
        }
        viewModelScope.launch {
            interactor.prepare(url).collect { preparationState ->
                when (preparationState) {
                    PlayerState.Prepared -> {
                        _screenState.postValue(
                            _screenState.value?.copy(playerState = PlayerState.Prepared) ?:
                            PlayerScreenState(
                                playerState = PlayerState.Prepared,
                                trackTitle = title,
                                trackArtist = artist,
                                currentTime = "00:00",
                                isFavorite = _screenState.value?.isFavorite ?: false
                            )
                        )
                    }
                    else -> {
                    }
                }
            }
        }

        viewModelScope.launch {
            interactor.observeProgress().collect { time ->
                _screenState.postValue(
                    _screenState.value?.copy(currentTime = time) ?: return@collect
                )
            }
        }
        viewModelScope.launch {
            interactor.observeCompletion().collect {
                _screenState.postValue(
                    _screenState.value?.copy(
                        playerState = PlayerState.Completed,
                        currentTime = "00:00"
                    ) ?: return@collect
                )
            }
        }
    }
    fun togglePlayback() {
        when (_screenState.value?.playerState) {
            PlayerState.Playing -> pause()
            PlayerState.Prepared, PlayerState.Paused -> play()
            PlayerState.Completed -> preparePlayer(
                currentUrl,
                _screenState.value?.trackTitle ?: "",
                _screenState.value?.trackArtist ?: "",
                currentTrack ?: return
            )
            else -> {}
        }
    }

    fun play() {
        interactor.play()
        _screenState.postValue(_screenState.value?.copy(playerState = PlayerState.Playing))
    }

    fun pause() {
        interactor.pause()
        _screenState.postValue(_screenState.value?.copy(playerState = PlayerState.Paused))
    }

    fun release() {
        interactor.release()
    }

    fun onFavoriteClicked() {
        val track = currentTrack ?: return

        viewModelScope.launch {
            val actualIsFavorite = favoriteInteractor.isTrackFavorite(track.id)
            val newIsFavorite = !actualIsFavorite

            if (newIsFavorite) {
                val trackWithCurrentTime = track.copy(
                    isFavorite = true,
                    dateAdded = System.currentTimeMillis()
                )
                favoriteInteractor.addTrack(trackWithCurrentTime)
            } else {
                favoriteInteractor.removeTrack(track)
            }

            currentTrack = track.copy(isFavorite = newIsFavorite)
            _screenState.postValue(
                _screenState.value?.copy(isFavorite = newIsFavorite)
            )
        }
    }
}
