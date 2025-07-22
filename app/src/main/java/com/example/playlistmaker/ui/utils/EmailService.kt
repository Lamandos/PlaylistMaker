package com.example.playlistmaker.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

class EmailService {
    fun sendEmail(
        context: Context,
        recipient: String,
        subject: String,
        body: String
    ) {
        val uri = Uri.parse("mailto:").buildUpon()
            .appendQueryParameter("to", recipient)
            .appendQueryParameter("subject", subject)
            .appendQueryParameter("body", body)
            .build()

        val emailIntent = Intent(Intent.ACTION_SENDTO, uri)
        context.startActivity(emailIntent)
    }
}