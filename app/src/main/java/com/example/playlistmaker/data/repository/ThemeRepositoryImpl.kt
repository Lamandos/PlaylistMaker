package com.example.playlistmaker.data.repository

import android.content.SharedPreferences
import com.example.playlistmaker.domain.api.ThemeRepository

class ThemeRepositoryImpl(
    private val sharedPrefs: SharedPreferences
) : ThemeRepository {

    companion object {
        private const val KEY_DARK_MODE = "dark_mode"
        private const val UNDEFINED = -1
    }

    override fun saveTheme(enabled: Boolean) {
        sharedPrefs.edit().putBoolean(KEY_DARK_MODE, enabled).apply()
    }

    override fun getCurrentTheme(): Boolean {
        return sharedPrefs.getBoolean(KEY_DARK_MODE, false)
    }

    override fun hasUserSetTheme(): Boolean {
        return sharedPrefs.contains(KEY_DARK_MODE)
    }
}
