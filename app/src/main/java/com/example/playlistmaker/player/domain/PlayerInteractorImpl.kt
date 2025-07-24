package com.example.playlistmaker.player.domain

class PlayerInteractorImpl(
    private val repository: PlayerRepository
) : PlayerInteractor {

    override fun prepare(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        repository.prepare(url, onPrepared, onCompletion)
    }

    override fun play() = repository.play()

    override fun pause() = repository.pause()

    override fun release() = repository.release()

    override fun isPlaying(): Boolean = repository.isPlaying()

    override fun currentPosition(): Int = repository.currentPosition()

    override fun seekTo(position: Int) = repository.seekTo(position)
}