package com.example.playlistmaker.db.domain

import org.koin.dsl.module

val domainModule = module {
    single<FavoriteTracksInteractor> { FavoriteTracksInteractorImpl(get()) }
}
