package com.example.playlistmaker.db.di

import com.example.playlistmaker.db.data.FavoriteTracksRepositoryImpl
import com.example.playlistmaker.db.domain.FavoriteTracksRepository
import com.example.playlistmaker.playlist.domain.PlaylistRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<FavoriteTracksRepository> { FavoriteTracksRepositoryImpl(get()) }
    single { PlaylistRepository(get(), get()) }
}
