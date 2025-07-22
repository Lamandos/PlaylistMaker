package com.example.playlistmaker.ui.view

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.TrackBinding
import com.example.playlistmaker.presentation.TrackParcelable
import com.example.playlistmaker.ui.utils.formatTrackTime

class TrackViewHolder(
    private val binding: TrackBinding,
    private val onTrackClick: (TrackParcelable) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(track: TrackParcelable) {
        binding.song.text = track.trackName
        binding.artistName.text = track.artistName
        binding.trackTime.text = formatTrackTime(track.trackTimeMillis)

        Glide.with(binding.cover.context)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder)
            .transform(RoundedCorners(16))
            .into(binding.cover)

        binding.root.setOnClickListener {
            onTrackClick(track)
        }
    }
}