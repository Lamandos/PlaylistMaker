package com.example.playlistmaker.settings.domain

class SettingsInteractor(private val settingsRepository: SettingsRepository) {

    fun isDarkModeEnabled(): Boolean {
        return settingsRepository.getCurrentTheme()
    }

    fun setDarkMode(enabled: Boolean) {
        settingsRepository.saveTheme(enabled)
    }

    fun hasUserSetTheme(): Boolean {
        return settingsRepository.hasUserSetTheme()
    }
}