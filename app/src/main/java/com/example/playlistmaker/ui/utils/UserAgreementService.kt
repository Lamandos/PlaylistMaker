package com.example.playlistmaker.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

class UserAgreementService {
    fun openUserAgreement(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }
}