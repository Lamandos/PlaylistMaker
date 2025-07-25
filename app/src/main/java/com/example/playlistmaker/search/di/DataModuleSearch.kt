package com.example.playlistmaker.search.di


import com.example.playlistmaker.search.data.SearchHistoryRepository
import com.example.playlistmaker.search.data.SearchRepositoryImpl
import com.example.playlistmaker.search.data.TrackMapper
import com.example.playlistmaker.search.data.TrackRepositoryImpl
import com.example.playlistmaker.search.domain.SearchRepository
import com.example.playlistmaker.search.domain.TrackRepository
import org.koin.dsl.module


val dataModuleSearch = module {
    single { SearchHistoryRepository(get()) }

    single { TrackMapper() }

    single<TrackRepository> { TrackRepositoryImpl(get(), get()) }

    single<SearchRepository> { SearchRepositoryImpl(get(), get()) }
}