package com.example.playlistmaker.utils

import android.content.Context

fun Context.dpToPx(dp: Int): Int {
    return (dp * resources.displayMetrics.density).toInt()
}