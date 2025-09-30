package com.example.playlistmaker.player.di

import com.example.playlistmaker.player.ui.view_model.PlayerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val uiModulePlayer = module {
    viewModel { PlayerViewModel(get(), get(), get()) }
}
