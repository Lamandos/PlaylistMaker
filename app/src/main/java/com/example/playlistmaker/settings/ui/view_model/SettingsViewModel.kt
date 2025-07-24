package com.example.playlistmaker.settings.ui.viewmodel

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.settings.domain.SettingsInteractor

class SettingsViewModel(private val settingsInteractor: SettingsInteractor) : ViewModel() {

    private val _isDarkModeEnabled = MutableLiveData<Boolean>()
    val isDarkModeEnabled: LiveData<Boolean> = _isDarkModeEnabled

    init {
        _isDarkModeEnabled.value = settingsInteractor.isDarkModeEnabled()
    }

    fun toggleTheme(isDarkMode: Boolean) {
        settingsInteractor.setDarkMode(isDarkMode)
        _isDarkModeEnabled.value = isDarkMode

        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}
