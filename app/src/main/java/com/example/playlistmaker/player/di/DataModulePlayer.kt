package com.example.playlistmaker.player.di

import com.example.playlistmaker.player.data.MediaPlayerController
import com.example.playlistmaker.player.data.PlayerRepositoryImpl
import com.example.playlistmaker.player.domain.PlayerRepository
import org.koin.dsl.module

val dataModulePlayer = module {
    factory { MediaPlayerController() }
    single<PlayerRepository> { PlayerRepositoryImpl(get()) }
}
