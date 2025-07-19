package com.example.playlistmaker.presentation.viewmodel

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.interactor.PlayerInteractor
import com.example.playlistmaker.presentation.state.PlayerState
import com.example.playlistmaker.ui.utils.formatTrackTime

class PlayerViewModel(
    private val player: PlayerInteractor
) : ViewModel() {

    private val handler = Handler(Looper.getMainLooper())

    private val _playerState = MutableLiveData<PlayerState>(PlayerState.Default)
    val playerState: LiveData<PlayerState> = _playerState

    private val _currentTime = MutableLiveData("00:00")
    val currentTime: LiveData<String> = _currentTime

    private var playbackPosition = 0

    private val progressRunnable = object : Runnable {
        override fun run() {
            if (player.isPlaying()) {
                _currentTime.postValue(formatTrackTime(player.currentPosition().toLong()))
                handler.postDelayed(this, 300L)
            }
        }
    }

    fun preparePlayer(url: String) {
        player.prepare(
            url,
            onPrepared = {
                _playerState.postValue(PlayerState.Prepared)
            },
            onCompletion = {
                _playerState.postValue(PlayerState.Completed)
                _currentTime.postValue(formatTrackTime(0))
                handler.removeCallbacks(progressRunnable)
            }
        )
    }

    fun togglePlayback() {
        when (_playerState.value) {
            PlayerState.Playing -> pause()
            PlayerState.Prepared, PlayerState.Paused -> play()
            else -> {}
        }
    }

    private fun play() {
        player.seekTo(playbackPosition)
        player.play()
        _playerState.postValue(PlayerState.Playing)
        handler.post(progressRunnable)
    }

    fun pause() {
        playbackPosition = player.currentPosition()
        player.pause()
        _playerState.postValue(PlayerState.Paused)
        handler.removeCallbacks(progressRunnable)
    }

    fun release() {
        handler.removeCallbacksAndMessages(null)
        player.release()
    }
}