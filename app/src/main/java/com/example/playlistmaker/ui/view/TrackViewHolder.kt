package com.example.playlistmaker.ui.view

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.utils.dpToPx
import com.example.playlistmaker.ui.utils.formatTrackTime

class TrackViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.track, parent, false)
) {
    private val trackName: TextView = itemView.findViewById(R.id.song)
    private val artistName: TextView = itemView.findViewById(R.id.artist_name)
    private val trackTime: TextView = itemView.findViewById(R.id.track_time)
    private val coverImage: ImageView = itemView.findViewById(R.id.cover)

    private val defaultPlaceholder = ContextCompat.getDrawable(itemView.context, R.drawable.placeholder)

    fun bind(track: Track) {
        trackName.text = track.trackName
        artistName.text = track.artistName
        trackTime.text = formatTrackTime(track.trackTimeMillis)
        loadCoverImage(track.artworkUrl100)
    }

    private fun loadCoverImage(url: String?) {
        Glide.with(itemView)
            .load(url)
            .transform(
                CenterCrop(),
                RoundedCorners(itemView.context.dpToPx(COVER_CORNER_RADIUS_DP))
            )
            .placeholder(defaultPlaceholder)
            .error(defaultPlaceholder)
            .fallback(defaultPlaceholder)
            .into(coverImage)
    }

    companion object {
        private const val COVER_CORNER_RADIUS_DP = 2
    }
}