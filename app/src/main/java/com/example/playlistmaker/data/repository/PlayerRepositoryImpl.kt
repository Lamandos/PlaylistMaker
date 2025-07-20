package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.player.MediaPlayerController
import com.example.playlistmaker.domain.api.PlayerRepository

class PlayerRepositoryImpl : PlayerRepository {

    private val mediaPlayerController = MediaPlayerController()

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
