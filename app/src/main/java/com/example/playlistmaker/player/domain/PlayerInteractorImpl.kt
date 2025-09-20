package com.example.playlistmaker.player.domain

import com.example.playlistmaker.player.PlayerState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.example.playlistmaker.utils.formatTrackTime

class PlayerInteractorImpl(
    private val repository: PlayerRepository
) : PlayerInteractor {

    override fun prepare(url: String): Flow<PlayerState> = repository.preparePlayer(url)

    override fun observeProgress(): Flow<String> =
        repository.playbackProgress().map { millis -> formatTrackTime(millis.toLong()) }

    override fun observeCompletion(): Flow<Unit> = repository.completionEvents()

    override fun play() = repository.play()
    override fun pause() = repository.pause()
    override fun release() = repository.release()
    override fun seekTo(positionMs: Int) = repository.seekTo(positionMs)
}
