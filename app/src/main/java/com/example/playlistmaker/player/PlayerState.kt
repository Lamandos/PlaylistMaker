package com.example.playlistmaker.player

sealed class PlayerState {
    object Default : PlayerState()
    object Preparing : PlayerState()
    object Prepared : PlayerState()
    object Playing : PlayerState()
    object Paused : PlayerState()
    object Completed : PlayerState()
}