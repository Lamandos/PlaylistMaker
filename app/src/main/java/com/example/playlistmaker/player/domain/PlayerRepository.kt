package com.example.playlistmaker.player.domain

import com.example.playlistmaker.player.PlayerState
import kotlinx.coroutines.flow.Flow

interface PlayerRepository {
    fun preparePlayer(url: String): Flow<PlayerState>
    fun playbackProgress(): Flow<Int>
    fun completionEvents(): Flow<Unit>
    fun play()
    fun pause()
    fun release()
    fun seekTo(positionMs: Int)
    fun isPlaying(): Boolean
    fun currentPosition(): Int
}