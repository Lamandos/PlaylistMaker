package com.example.playlistmaker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.player.PlayerState
import com.example.playlistmaker.player.domain.PlayerInteractor
import kotlinx.coroutines.launch

data class PlayerScreenState(
    val playerState: PlayerState = PlayerState.Default,
    val currentTime: String = "00:00",
    val trackTitle: String = "",
    val trackArtist: String = ""
)

class PlayerViewModel(
    private val interactor: PlayerInteractor
) : ViewModel() {

    private val _screenState = MutableLiveData<PlayerScreenState>()
    val screenState: LiveData<PlayerScreenState> = _screenState

    private var currentUrl: String = ""

    fun preparePlayer(url: String, title: String, artist: String) {
        currentUrl = url

        viewModelScope.launch {
            interactor.prepare(url).collect { state ->
                _screenState.postValue(
                    PlayerScreenState(
                        playerState = state,
                        trackTitle = title,
                        trackArtist = artist,
                        currentTime = if (state == PlayerState.Completed) "00:00" else "00:00"
                    )
                )
            }
        }

        viewModelScope.launch {
            interactor.observeProgress().collect { time ->
                _screenState.postValue(_screenState.value?.copy(currentTime = time))
            }
        }

        viewModelScope.launch {
            interactor.observeCompletion().collect {
                _screenState.postValue(
                    _screenState.value?.copy(playerState = PlayerState.Completed, currentTime = "00:00")
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
                _screenState.value?.trackArtist ?: ""
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
}
