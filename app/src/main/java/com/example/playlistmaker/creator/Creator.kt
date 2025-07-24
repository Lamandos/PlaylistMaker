package com.example.playlistmaker.creator

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.search.data.ITunesApi
import com.example.playlistmaker.search.data.SearchHistoryRepository
import com.example.playlistmaker.search.data.TrackMapper
import com.example.playlistmaker.search.data.TrackRepositoryImpl
import com.example.playlistmaker.search.domain.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.SearchTracksInteractor
import com.example.playlistmaker.player.data.PlayerRepositoryImpl
import com.example.playlistmaker.player.domain.PlayerInteractorImpl
import com.example.playlistmaker.search.ui.view_model.SearchViewModel
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.player.ui.view_model.PlayerViewModel
import com.example.playlistmaker.search.data.SearchRepositoryImpl
import com.example.playlistmaker.search.domain.SearchRepository
import com.example.playlistmaker.search.domain.TrackRepository
import com.example.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import com.example.playlistmaker.sharing.domain.impl.ExternalNavigatorImpl
import com.example.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import com.example.playlistmaker.sharing.domain.model.SharingInteractor
import com.google.gson.Gson
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Creator {
    private lateinit var appContext: Context

    private val gson: Gson by lazy {
        Gson()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val iTunesApi: ITunesApi by lazy {
        retrofit.create(ITunesApi::class.java)
    }

    private val trackMapper: TrackMapper by lazy {
        TrackMapper()
    }

    private val trackRepository: TrackRepository by lazy {
        TrackRepositoryImpl(iTunesApi, trackMapper)
    }

    private val searchRepository: SearchRepository by lazy {
        SearchRepositoryImpl(trackRepository, searchHistoryRepository)
    }

    val searchTracksInteractor: SearchTracksInteractor by lazy {
        SearchTracksInteractor(searchRepository)
    }

    private val prefs by lazy {
        appContext.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
    }

    private val searchHistoryRepository: SearchHistoryRepository by lazy {
        SearchHistoryRepository(prefs)
    }

    val searchHistoryInteractor: SearchHistoryInteractor by lazy {
        SearchHistoryInteractor(searchHistoryRepository)
    }

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    fun providePlayerViewModelFactory(): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repository = PlayerRepositoryImpl()
                val interactor = PlayerInteractorImpl(repository)
                return PlayerViewModel(interactor) as T
            }
        }
    }

    fun provideThemeInteractor(context: Context): SettingsInteractor {
        val sharedPrefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        val repository = SettingsRepositoryImpl(sharedPrefs)
        return SettingsInteractor(repository)
    }

    fun provideSearchViewModelFactory(): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SearchViewModel(
                    Creator.searchTracksInteractor,
                    Creator.searchHistoryInteractor
                ) as T
            }
        }
    }

    fun provideSettingsViewModelFactory(context: Context): ViewModelProvider.Factory {
        val themeInteractor = provideThemeInteractor(context)  // Передаем context
        return object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
                    return SettingsViewModel(themeInteractor) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }

    private val externalNavigator: ExternalNavigatorImpl by lazy {
        ExternalNavigatorImpl()
    }

    fun provideSharingInteractor(): SharingInteractor {
        return SharingInteractorImpl(externalNavigator)
    }
}
