package com.example.playlistmaker.sharing.domain.model

import android.content.Context
import com.example.playlistmaker.R

data class EmailData(
    val recipient: String,
    val subject: String,
    val body: String
)
fun Context.createSupportEmail(): EmailData = EmailData(
    recipient = getString(R.string.rec_email),
    subject = getString(R.string.mail_title),
    body = getString(R.string.mail_text)
)