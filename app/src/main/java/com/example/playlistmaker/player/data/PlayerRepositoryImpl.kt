package com.example.playlistmaker.player.data

import com.example.playlistmaker.player.data.MediaPlayerController
import com.example.playlistmaker.player.domain.PlayerRepository

class PlayerRepositoryImpl(
    private val mediaPlayerController: MediaPlayerController
) : PlayerRepository {



    override fun prepare(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        mediaPlayerController.prepare(url, onPrepared, onCompletion)
    }

    override fun play() = mediaPlayerController.play()

    override fun pause() = mediaPlayerController.pause()

    override fun release() = mediaPlayerController.release()

    override fun isPlaying(): Boolean = mediaPlayerController.isPlaying()

    override fun currentPosition(): Int = mediaPlayerController.currentPosition()

    override fun seekTo(position: Int) = mediaPlayerController.seekTo(position)
}