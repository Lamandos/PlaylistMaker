package com.example.playlistmaker.db.di

import com.example.playlistmaker.db.data.FavoriteTracksRepositoryImpl
import com.example.playlistmaker.db.domain.FavoriteTracksRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<FavoriteTracksRepository> { FavoriteTracksRepositoryImpl(get()) }
}
