package com.example.playlistmaker

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.db.di.dataModule
import com.example.playlistmaker.db.di.repositoryModule
import com.example.playlistmaker.db.domain.domainModule
import com.example.playlistmaker.media.di.uiModuleMedia
import com.example.playlistmaker.player.di.dataModulePlayer
import com.example.playlistmaker.player.di.domainModulePlayer
import com.example.playlistmaker.player.di.uiModulePlayer
import com.example.playlistmaker.search.di.dataModuleSearch
import com.example.playlistmaker.search.di.domainModuleSearch
import com.example.playlistmaker.search.di.networkModule
import com.example.playlistmaker.search.di.uiModuleSearch
import com.example.playlistmaker.settings.di.dataModuleSettings
import com.example.playlistmaker.settings.di.domainModuleSettings
import com.example.playlistmaker.settings.di.sharingModule
import com.example.playlistmaker.settings.di.uiModuleSettings
import com.example.playlistmaker.settings.domain.SettingsInteractor
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this

        startKoin {
            androidContext(this@App)
            modules(repositoryModule, domainModule, dataModule, dataModulePlayer, domainModulePlayer, uiModulePlayer,
                dataModuleSettings, domainModuleSettings, uiModuleSettings, sharingModule,
                networkModule, dataModuleSearch, domainModuleSearch, uiModuleSearch, uiModuleMedia)
        }

        val themeInteractor: SettingsInteractor by inject()

        if (themeInteractor.hasUserSetTheme()) {
            val isDark = themeInteractor.isDarkModeEnabled()
            val mode = if (isDark) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
            AppCompatDelegate.setDefaultNightMode(mode)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    companion object {
        private var instance: App? = null

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }
}