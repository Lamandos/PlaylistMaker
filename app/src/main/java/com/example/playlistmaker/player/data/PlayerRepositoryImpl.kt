package com.example.playlistmaker.player.data

import com.example.playlistmaker.player.PlayerState
import com.example.playlistmaker.player.domain.PlayerRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import kotlinx.coroutines.delay

class PlayerRepositoryImpl(
    private val mediaPlayerController: MediaPlayerController
) : PlayerRepository {

    override fun preparePlayer(url: String): Flow<PlayerState> =
        mediaPlayerController.prepare(url).map { event ->
            when (event) {
                PlayerEvent.Preparing -> PlayerState.Preparing
                PlayerEvent.Prepared -> PlayerState.Prepared
                PlayerEvent.Completed -> PlayerState.Completed
            }
        }

    override fun playbackProgress(): Flow<Int> = flow {
        val context = currentCoroutineContext()
        while (context.isActive) {
            if (mediaPlayerController.isPlaying()) {
                emit(mediaPlayerController.currentPosition())
            }
            delay(300L)
        }
    }

    override fun completionEvents(): Flow<Unit> = callbackFlow {
        val listener: (PlayerEvent) -> Unit = { event ->
            if (event is PlayerEvent.Completed) trySend(Unit).isSuccess
        }

        mediaPlayerController.addEventListener(listener)

        awaitClose {
            mediaPlayerController.removeEventListener(listener)
        }
    }

    override fun play() = mediaPlayerController.play()
    override fun pause() = mediaPlayerController.pause()
    override fun release() = mediaPlayerController.release()
    override fun seekTo(positionMs: Int) = mediaPlayerController.seekTo(positionMs)
    override fun isPlaying(): Boolean = mediaPlayerController.isPlaying()
    override fun currentPosition(): Int = mediaPlayerController.currentPosition()
}
