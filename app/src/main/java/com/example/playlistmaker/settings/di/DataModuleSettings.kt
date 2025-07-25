package com.example.playlistmaker.settings.di

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.SettingsRepository
import org.koin.dsl.module

val dataModuleSettings = module {
    single<SharedPreferences> {
        get<Context>().getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)
    }

    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
}