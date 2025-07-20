package com.example.playlistmaker.domain.api

interface ThemeRepository {
    fun saveTheme(enabled: Boolean)
    fun getCurrentTheme(): Boolean
    fun hasUserSetTheme(): Boolean
}