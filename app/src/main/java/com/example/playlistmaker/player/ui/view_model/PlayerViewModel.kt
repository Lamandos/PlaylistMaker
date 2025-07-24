package com.example.playlistmaker.player.ui.view_model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.player.domain.PlayerInteractor
import com.example.playlistmaker.player.PlayerState
import com.example.playlistmaker.utils.formatTrackTime


data class PlayerScreenState(
    val playerState: PlayerState = PlayerState.Default,
    val currentTime: String = "00:00",
    val trackTitle: String = "",
    val trackArtist: String = ""
)

class PlayerViewModel(
    private val interactor: PlayerInteractor
) : ViewModel() {
    private var currentUrl: String = ""
    private val handler = Handler(Looper.getMainLooper())

    private val _screenState = MutableLiveData<PlayerScreenState>()
    val screenState: LiveData<PlayerScreenState> = _screenState

    private var playbackPosition = 0

    private val progressRunnable = object : Runnable {
        override fun run() {
            if (interactor.isPlaying()) {
                _screenState.value?.let {
                    _screenState.postValue(
                        it.copy(currentTime = formatTrackTime(interactor.currentPosition().toLong()))
                    )
                }
                handler.postDelayed(this, 300L)
            }
        }
    }

    fun preparePlayer(url: String, title: String, artist: String) {
        currentUrl = url
        _screenState.value = PlayerScreenState(
            playerState = PlayerState.Preparing,
            trackTitle = title,
            trackArtist = artist

        )

        interactor.prepare(
            url,
            onPrepared = {
                _screenState.postValue(
                    PlayerScreenState(
                        playerState = PlayerState.Prepared,
                        trackTitle = title,
                        trackArtist = artist,
                        currentTime = "00:00"
                    )
                )
            },
            onCompletion = {
                _screenState.postValue(
                    _screenState.value?.copy(
                        playerState = PlayerState.Completed,
                        currentTime = "00:00"
                    )
                )
                preparePlayer(currentUrl, title, artist)
            }
        )
    }

    fun togglePlayback() {
        when (screenState.value?.playerState) {
            PlayerState.Playing -> pause()
            PlayerState.Prepared, PlayerState.Paused -> play()
            PlayerState.Completed -> {
                preparePlayer(currentUrl,
                    screenState.value?.trackTitle ?: "",
                    screenState.value?.trackArtist ?: "")
            }
            PlayerState.Preparing -> {}
            else -> {}
        }
    }
    private fun play() {
        if (interactor.isPlaying()) return

        interactor.seekTo(playbackPosition)
        interactor.play()
        _screenState.postValue(
            screenState.value!!.copy(playerState = PlayerState.Playing)
        )
        startProgressUpdates()
    }
    fun pause() {
        playbackPosition = interactor.currentPosition()
        interactor.pause()
        _screenState.value?.let {
            _screenState.postValue(it.copy(playerState = PlayerState.Paused))
        }
        stopProgressUpdates()
    }
    fun release() {
        stopProgressUpdates()
        interactor.release()
    }
    private fun startProgressUpdates() {
        handler.post(progressRunnable)
    }
    private fun stopProgressUpdates() {
        handler.removeCallbacks(progressRunnable)
    }
}
