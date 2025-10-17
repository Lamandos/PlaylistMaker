package com.example.playlistmaker.playlist_tracks.ui.module

import com.example.playlistmaker.playlist.domain.PlaylistRepository
import com.example.playlistmaker.playlist_tracks.ui.view_model.PlaylistTracksViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playlistTracksModule = module {
    viewModel { PlaylistTracksViewModel(get(),get()) }
}
