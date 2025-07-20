package com.example.playlistmaker

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.data.mapper.TrackMapper
import com.example.playlistmaker.data.network.ITunesApi
import com.example.playlistmaker.data.repository.PlayerRepositoryImpl
import com.example.playlistmaker.data.repository.SearchHistory
import com.example.playlistmaker.data.repository.ThemeRepositoryImpl
import com.example.playlistmaker.domain.api.ThemeRepository
import com.example.playlistmaker.data.repository.TrackRepositoryImpl
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.interactor.PlayerInteractorImpl
import com.example.playlistmaker.domain.interactor.SearchHistoryInteractor
import com.example.playlistmaker.domain.interactor.SearchTracksInteractor
import com.example.playlistmaker.domain.interactor.ThemeInteractor
import com.example.playlistmaker.presentation.viewmodel.PlayerViewModel
import com.example.playlistmaker.ui.di.App
import com.example.playlistmaker.ui.utils.ShareService
import com.example.playlistmaker.utils.EmailService
import com.example.playlistmaker.utils.UserAgreementService
import com.google.gson.Gson
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object Creator {
    private val gson: Gson by lazy {
        Gson()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com/")
            .addConverterFactory(GsonConverterFactory.create(gson))
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

    val searchTracksInteractor: SearchTracksInteractor by lazy {
        SearchTracksInteractor(trackRepository)
    }
    private val searchHistoryRepository: SearchHistory by lazy {
        SearchHistory(prefs)
    }
    val searchHistoryInteractor: SearchHistoryInteractor by lazy {
        SearchHistoryInteractor(searchHistoryRepository)
    }
    private val shareService: ShareService by lazy { ShareService() }
    fun provideShareService(): ShareService = shareService

    private val userAgreementService: UserAgreementService by lazy { UserAgreementService() }
    fun provideUserAgreementService(): UserAgreementService = userAgreementService

    private val prefs by lazy {
        App.applicationContext().getSharedPreferences("app_settings", Context.MODE_PRIVATE)
    }
    private val emailService by lazy { EmailService() }
    fun provideEmailService(): EmailService = emailService

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
    fun provideThemeInteractor(context: Context): ThemeInteractor {
        val sharedPrefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        val repository = ThemeRepositoryImpl(sharedPrefs)
        return ThemeInteractor(repository)
    }
}

