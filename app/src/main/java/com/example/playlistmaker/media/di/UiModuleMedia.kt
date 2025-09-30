package com.example.playlistmaker.media.di

import com.example.playlistmaker.db.data.AppDatabasePlaylist
import com.example.playlistmaker.media.ui.view_model.FavoritesViewModel
import com.example.playlistmaker.media.ui.view_model.MediaViewModel
import com.example.playlistmaker.media.ui.view_model.PlaylistsViewModel
import com.example.playlistmaker.playlist.domain.PlaylistInteractor
import com.example.playlistmaker.playlist.domain.PlaylistRepository
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val uiModuleMedia = module {
    viewModel { MediaViewModel() }
    viewModel { FavoritesViewModel(get()) }
    viewModel { PlaylistsViewModel(get()) }
    single { AppDatabasePlaylist.getDatabase(androidContext()) }
    single { get<AppDatabasePlaylist>().playlistDao() }
    single { get<AppDatabasePlaylist>().playlistTrackDao() } // <--- сюда
    single { PlaylistRepository(get(), get()) }
    single { PlaylistInteractor(get()) }

}