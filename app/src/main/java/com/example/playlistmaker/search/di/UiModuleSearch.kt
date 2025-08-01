package com.example.playlistmaker.search.di

import com.example.playlistmaker.search.ui.view_model.SearchViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val uiModuleSearch = module {
    viewModel {
        SearchViewModel(
            searchTracksInteractor = get(),
            searchHistoryInteractor = get()
        )
    }
}