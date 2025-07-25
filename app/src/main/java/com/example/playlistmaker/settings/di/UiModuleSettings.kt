package com.example.playlistmaker.settings.di


import com.example.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val uiModuleSettings = module {
    viewModel { SettingsViewModel(get(), get()) }
}