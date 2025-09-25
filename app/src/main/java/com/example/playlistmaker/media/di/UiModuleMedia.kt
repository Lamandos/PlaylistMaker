package com.example.playlistmaker.media.di

import com.example.playlistmaker.db.domain.FavoriteTracksInteractor
import com.example.playlistmaker.media.ui.view_model.FavoritesViewModel
import com.example.playlistmaker.media.ui.view_model.MediaViewModel
import com.example.playlistmaker.media.ui.view_model.PlaylistsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val uiModuleMedia = module {
    viewModel { MediaViewModel() }
    viewModel { FavoritesViewModel(get()) }
    viewModel { PlaylistsViewModel() }
}