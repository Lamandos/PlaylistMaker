package com.example.playlistmaker.player.domain

import com.example.playlistmaker.player.PlayerState
import kotlinx.coroutines.flow.Flow

interface PlayerInteractor {
    fun prepare(url: String): Flow<PlayerState>
    fun observeProgress(): Flow<String>
    fun observeCompletion(): Flow<Unit>
    fun play()
    fun pause()
    fun release()
    fun seekTo(positionMs: Int)
}