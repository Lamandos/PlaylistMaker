package com.example.playlistmaker.search.di

import com.example.playlistmaker.search.domain.SearchTracksInteractor
import com.example.playlistmaker.search.domain.SearchHistoryInteractor
import org.koin.dsl.module

val domainModuleSearch = module {
    factory { SearchTracksInteractor(get()) }
    factory { SearchHistoryInteractor(get()) }
}