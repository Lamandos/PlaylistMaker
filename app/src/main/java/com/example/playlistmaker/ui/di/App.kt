package com.example.playlistmaker.ui.di

import android.app.Application
import android.content.Context
import com.example.playlistmaker.Creator

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        Creator.themeInteractor.applyTheme()
    }

    companion object {
        private var instance: App? = null

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }
}