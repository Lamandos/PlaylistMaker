package com.example.playlistmaker.player.di

import com.example.playlistmaker.player.domain.PlayerInteractor
import com.example.playlistmaker.player.domain.PlayerInteractorImpl
import org.koin.dsl.module

val domainModulePlayer = module {
    single<PlayerInteractor> { PlayerInteractorImpl(get()) }
}
