package com.example.playlistmaker.ui.utils

import android.content.Context
import android.content.Intent
import com.example.playlistmaker.R

class ShareService {
    fun shareApp(context: Context, message: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, message)
        }
        context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share_opt)))
    }
}