package com.example.playlistmaker.settings.di

import com.example.playlistmaker.sharing.domain.impl.ExternalNavigator
import com.example.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import com.example.playlistmaker.sharing.domain.model.SharingInteractor
import com.example.playlistmaker.sharing.domain.impl.ExternalNavigatorImpl
import org.koin.dsl.module

val sharingModule = module {
    single<ExternalNavigator> { ExternalNavigatorImpl() }

    single<SharingInteractor> { SharingInteractorImpl(get()) }
}