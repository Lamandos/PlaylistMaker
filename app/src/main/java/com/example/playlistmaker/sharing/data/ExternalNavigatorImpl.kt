package com.example.playlistmaker.sharing.domain.impl

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.R
import com.example.playlistmaker.sharing.domain.model.EmailData

class ExternalNavigatorImpl : ExternalNavigator {

    override fun shareLink(context: Context, message: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, message)
        }
        context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share_opt)))
    }

    override fun openLink(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }

    override fun openEmail(context: Context, emailData: EmailData) {
        val uri = Uri.parse("mailto:").buildUpon()
            .appendQueryParameter("to", emailData.recipient)
            .appendQueryParameter("subject", emailData.subject)
            .appendQueryParameter("body", emailData.body)
            .build()

        val emailIntent = Intent(Intent.ACTION_SENDTO, uri)
        context.startActivity(emailIntent)
    }
}