package com.example.playlistmaker.search.di


import com.example.playlistmaker.search.data.SearchHistoryRepository
import com.example.playlistmaker.search.data.SearchRepositoryImpl
import com.example.playlistmaker.search.data.TrackMapper
import com.example.playlistmaker.search.data.TrackRepositoryImpl
import com.example.playlistmaker.search.domain.SearchRepository
import com.example.playlistmaker.search.domain.TrackRepository
import com.google.gson.Gson
import org.koin.dsl.module


val dataModuleSearch = module {
    single { Gson() }
    single { SearchHistoryRepository(get(), get()) }

    single { TrackMapper() }

    factory<TrackRepository> { TrackRepositoryImpl(get(), get()) }
    factory<SearchRepository> { SearchRepositoryImpl(get(), get()) }
}