package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.domain.api.ThemeRepository

class ThemeInteractor(private val themeRepository: ThemeRepository) {

    fun isDarkModeEnabled(): Boolean {
        return themeRepository.getCurrentTheme()
    }

    fun setDarkMode(enabled: Boolean) {
        themeRepository.saveTheme(enabled)
    }

    fun hasUserSetTheme(): Boolean {
        return themeRepository.hasUserSetTheme()
    }
}