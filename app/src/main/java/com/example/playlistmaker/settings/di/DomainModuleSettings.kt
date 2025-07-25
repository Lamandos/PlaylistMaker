package com.example.playlistmaker.settings.di

import com.example.playlistmaker.settings.domain.SettingsInteractor
import org.koin.dsl.module

val domainModuleSettings  = module {
    single { SettingsInteractor(get()) }
}