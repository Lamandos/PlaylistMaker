package com.example.playlistmaker.settings.domain

interface SettingsRepository {
    fun saveTheme(enabled: Boolean)
    fun getCurrentTheme(): Boolean
    fun hasUserSetTheme(): Boolean
}