package com.example.playlistmaker.player.di

import android.media.AudioAttributes
import android.media.MediaPlayer
import com.example.playlistmaker.player.data.MediaPlayerController
import com.example.playlistmaker.player.data.PlayerRepositoryImpl
import com.example.playlistmaker.player.domain.PlayerRepository
import org.koin.dsl.module

val dataModulePlayer = module {
    factory {
        { ->
            MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
            }
        }
    }

    factory { MediaPlayerController(get()) }

    single<PlayerRepository> { PlayerRepositoryImpl(get()) }
}