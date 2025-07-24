package com.example.playlistmaker.sharing.domain.impl

import android.content.Context
import com.example.playlistmaker.sharing.domain.model.EmailData
import com.example.playlistmaker.sharing.domain.model.SharingInteractor

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator
) : SharingInteractor {

    override fun shareApp(context: Context, message: String) {
        externalNavigator.shareLink(context, message)
    }

    override fun openTerms(context: Context, url: String) {
        externalNavigator.openLink(context, url)
    }

    override fun openSupport(context: Context, emailData: EmailData) {
        externalNavigator.openEmail(context, emailData)
    }
}