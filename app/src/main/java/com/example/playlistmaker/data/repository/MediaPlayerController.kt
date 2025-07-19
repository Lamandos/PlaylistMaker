package com.example.playlistmaker.data.player

import android.media.AudioAttributes
import android.media.MediaPlayer
import com.example.playlistmaker.domain.interactor.PlayerInteractor

class MediaPlayerController : PlayerInteractor {

    private var mediaPlayer: MediaPlayer? = null
    private var isPrepared = false

    override fun prepare(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        release()

        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
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

    override fun play() {
        if (isPrepared) {
            mediaPlayer?.start()
        }
    }

    override fun pause() {
        if (isPrepared && mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
        }
    }

    override fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
        isPrepared = false
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying ?: false
    }

    override fun currentPosition(): Int {
        return mediaPlayer?.currentPosition ?: 0
    }

    override fun seekTo(position: Int) {
        if (isPrepared) {
            mediaPlayer?.seekTo(position)
        }
    }
}
