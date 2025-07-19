package com.example.playlistmaker.ui.view



object ImageUtils {
    fun getCoverArtwork(url: String): String {
        return url.replaceAfterLast('/', "512x512bb.jpg")
    }
}