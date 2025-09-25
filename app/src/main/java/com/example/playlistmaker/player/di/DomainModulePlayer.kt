package com.example.playlistmaker.player.di

import com.example.playlistmaker.db.domain.FavoriteTracksInteractor
import com.example.playlistmaker.db.domain.FavoriteTracksInteractorImpl
import com.example.playlistmaker.player.domain.PlayerInteractor
import com.example.playlistmaker.player.domain.PlayerInteractorImpl
import org.koin.dsl.module

val domainModulePlayer = module {
    single<PlayerInteractor> { PlayerInteractorImpl(get()) }
    single<FavoriteTracksInteractor> { FavoriteTracksInteractorImpl(get()) }
}
