package com.example.playlistmaker.sharing.domain.impl

import android.content.Context
import com.example.playlistmaker.sharing.domain.model.EmailData

interface ExternalNavigator {
    fun shareLink(context: Context, message: String)
    fun openLink(context: Context, url: String)
    fun openEmail(context: Context, emailData: EmailData)
}
