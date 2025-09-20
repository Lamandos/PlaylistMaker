package com.example.playlistmaker.player.data

import android.media.MediaPlayer
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class MediaPlayerController(
    private val mediaPlayerProvider: () -> MediaPlayer
) {
    private var mediaPlayer: MediaPlayer? = null
    private var isPrepared = false

    private val listeners = mutableListOf<(PlayerEvent) -> Unit>()

    fun prepare(url: String): Flow<PlayerEvent> = callbackFlow {
        release()

        mediaPlayer = mediaPlayerProvider().apply {
            setDataSource(url)

            setOnPreparedListener {
                isPrepared = true
                val event = PlayerEvent.Prepared
                trySend(event).isSuccess
                notifyListeners(event)
            }

            setOnCompletionListener {
                isPrepared = false
                val event = PlayerEvent.Completed
                trySend(event).isSuccess
                notifyListeners(event)
            }

            prepareAsync()
        }

        val preparingEvent = PlayerEvent.Preparing
        trySend(preparingEvent).isSuccess
        notifyListeners(preparingEvent)

        awaitClose { release() }
    }

    fun play() { if (isPrepared) mediaPlayer?.start() }
    fun pause() { if (isPrepared && mediaPlayer?.isPlaying == true) mediaPlayer?.pause() }
    fun release() { mediaPlayer?.release(); mediaPlayer = null; isPrepared = false }
    fun isPlaying(): Boolean = mediaPlayer?.isPlaying ?: false
    fun currentPosition(): Int = mediaPlayer?.currentPosition ?: 0
    fun seekTo(position: Int) { if (isPrepared) mediaPlayer?.seekTo(position) }

    fun addEventListener(listener: (PlayerEvent) -> Unit) { listeners.add(listener) }
    fun removeEventListener(listener: (PlayerEvent) -> Unit) { listeners.remove(listener) }

    private fun notifyListeners(event: PlayerEvent) { listeners.forEach { it(event) } }
}

sealed class PlayerEvent {
    object Preparing : PlayerEvent()
    object Prepared : PlayerEvent()
    object Completed : PlayerEvent()
}
