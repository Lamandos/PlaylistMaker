package com.example.playlistmaker.domain.api

interface PlayerRepository {
    fun prepare(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit)
    fun play()
    fun pause()
    fun release()
    fun isPlaying(): Boolean
    fun currentPosition(): Int
    fun seekTo(position: Int)
}