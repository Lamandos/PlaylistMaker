package com.example.playlistmaker.db.di

import android.content.Context
import com.example.playlistmaker.db.data.AppDatabase
import com.example.playlistmaker.db.data.FavoriteTracksRepositoryImpl
import com.example.playlistmaker.db.domain.FavoriteTracksRepository
import org.koin.dsl.module

val dataModule = module {
    single { AppDatabase.getDatabase(get<Context>()) }

    single<FavoriteTracksRepository> {
        FavoriteTracksRepositoryImpl(get())
    }
}
