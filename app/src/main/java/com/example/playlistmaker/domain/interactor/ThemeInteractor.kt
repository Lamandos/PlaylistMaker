package com.example.playlistmaker.domain.interactor

import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.ui.di.App

class ThemeInteractor(
    private val sharedPreferences: SharedPreferences
) {
    private val THEME_KEY = "dark_mode"

    fun isDarkModeEnabled(): Boolean {
        return sharedPreferences.getBoolean(THEME_KEY, getSystemDefaultTheme())
    }

    fun setDarkMode(enabled: Boolean) {
        sharedPreferences.edit()
            .putBoolean(THEME_KEY, enabled)
            .apply()
    }

    fun applyTheme() {
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkModeEnabled()) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    private fun getSystemDefaultTheme(): Boolean {
        val currentNightMode = App.applicationContext().resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES
    }
}