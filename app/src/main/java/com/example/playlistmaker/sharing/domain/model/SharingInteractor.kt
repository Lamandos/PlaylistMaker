package com.example.playlistmaker.sharing.domain.model

import android.content.Context

interface SharingInteractor {
    fun shareApp(context: Context, message: String)
    fun openTerms(context: Context, url: String)
    fun openSupport(context: Context, emailData: EmailData)
}
