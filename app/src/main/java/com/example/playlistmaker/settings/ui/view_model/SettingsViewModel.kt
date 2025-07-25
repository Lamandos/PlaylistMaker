package com.example.playlistmaker.settings.ui.viewmodel

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.R
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.sharing.domain.model.SharingInteractor
import com.example.playlistmaker.sharing.domain.model.createSupportEmail

class SettingsViewModel(private val settingsInteractor: SettingsInteractor,
                        private val sharingInteractor: SharingInteractor
) : ViewModel() {

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
    fun shareApp(context: Context) {
        sharingInteractor.shareApp(context, context.getString(R.string.share_text))
    }

    fun openTerms(context: Context) {
        sharingInteractor.openTerms(context, context.getString(R.string.agreement_text))
    }

    fun openSupport(context: Context) {
        sharingInteractor.openSupport(context, context.createSupportEmail())
    }
}
