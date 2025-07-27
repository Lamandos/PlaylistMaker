package com.example.playlistmaker.player.data

import android.media.MediaPlayer

class MediaPlayerController(
    private val mediaPlayerProvider: () -> MediaPlayer
) {
    private var mediaPlayer: MediaPlayer? = null
    private var isPrepared = false

    fun prepare(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        release()

        mediaPlayer = mediaPlayerProvider().apply {
            setDataSource(url)
            setOnPreparedListener {
                isPrepared = true
                onPrepared()
            }
            setOnCompletionListener {
                isPrepared = false
                onCompletion()
            }
            prepareAsync()
        }
    }

    fun play() {
        if (isPrepared) mediaPlayer?.start()
    }

    fun pause() {
        if (isPrepared && mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
        }
    }

    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
        isPrepared = false
    }

    fun isPlaying(): Boolean = mediaPlayer?.isPlaying ?: false

    fun currentPosition(): Int = mediaPlayer?.currentPosition ?: 0

    fun seekTo(position: Int) {
        if (isPrepared) mediaPlayer?.seekTo(position)
    }
}