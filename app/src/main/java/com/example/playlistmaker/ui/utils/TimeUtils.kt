package com.example.playlistmaker.ui.utils

fun formatTrackTime(timeInMillis: Long): String {
    val minutes = timeInMillis / 60000
    val seconds = (timeInMillis % 60000) / 1000
    return String.format("%02d:%02d", minutes, seconds)
}