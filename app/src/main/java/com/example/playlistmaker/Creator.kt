package com.example.playlistmaker

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.data.mapper.TrackMapper
import com.example.playlistmaker.data.network.ITunesApi
import com.example.playlistmaker.data.player.MediaPlayerController
import com.example.playlistmaker.data.repository.SearchHistory
import com.example.playlistmaker.data.repository.TrackRepositoryImpl
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.interactor.PlayerInteractor
import com.example.playlistmaker.domain.interactor.SearchHistoryInteractor
import com.example.playlistmaker.domain.interactor.SearchTracksInteractor
import com.example.playlistmaker.domain.interactor.ThemeInteractor
import com.example.playlistmaker.presentation.TrackParcelable
import com.example.playlistmaker.presentation.viewmodel.PlayerViewModel
import com.example.playlistmaker.ui.activity.TrackAdapter
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

    val playerInteractor: PlayerInteractor by lazy {
        MediaPlayerController()
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

    private var historyInteractor: SearchHistoryInteractor? = null

    fun provideSearchHistoryInteractor(context: Context): SearchHistoryInteractor {
        if (historyInteractor == null) {
            val sharedPreferences = context.getSharedPreferences("playlist_prefs", Context.MODE_PRIVATE)
            val repository = SearchHistory(sharedPreferences)
            historyInteractor = SearchHistoryInteractor(repository)
        }
        return historyInteractor!!
    }

    fun provideTrackAdapter(onClick: (TrackParcelable) -> Unit): TrackAdapter {
        return TrackAdapter(mutableListOf(), onClick)
    }

    private val shareService: ShareService by lazy { ShareService() }
    fun provideShareService(): ShareService = shareService

    private val userAgreementService: UserAgreementService by lazy { UserAgreementService() }
    fun provideUserAgreementService(): UserAgreementService = userAgreementService

    private val prefs by lazy {
        App.applicationContext().getSharedPreferences("app_settings", Context.MODE_PRIVATE)
    }

    val themeInteractor by lazy {
        ThemeInteractor(prefs)
    }

    private val emailService by lazy { EmailService() }
    fun provideEmailService(): EmailService = emailService

    fun providePlayerViewModelFactory(): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(PlayerViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return PlayerViewModel(playerInteractor) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}

