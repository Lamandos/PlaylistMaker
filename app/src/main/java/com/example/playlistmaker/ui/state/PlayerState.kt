package com.example.playlistmaker.presentation.state

sealed class PlayerState {
    object Default : PlayerState()
    object Playing : PlayerState()
    object Paused : PlayerState()
    object Prepared : PlayerState()
    object Completed : PlayerState()
}