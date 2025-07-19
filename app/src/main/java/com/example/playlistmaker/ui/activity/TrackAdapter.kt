package com.example.playlistmaker.ui.activity

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.TrackBinding
import com.example.playlistmaker.presentation.TrackParcelable
import com.example.playlistmaker.ui.utils.formatTrackTime
class TrackAdapter(private var tracks: MutableList<TrackParcelable>, private val onTrackClick: (TrackParcelable) -> Unit) : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

//    private var isClickAllowed = true
//
//    private val handler = Handler(Looper.getMainLooper())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding = TrackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = tracks[position]
        holder.bind(track)
    }

    override fun getItemCount() = tracks.size

    fun updateTracks(newTracks: List<TrackParcelable>) {
        tracks.clear()
        tracks.addAll(newTracks)
        notifyDataSetChanged()
    }

//    fun clearTracks() {
//        tracks.clear()
//        notifyDataSetChanged()
//    }
//
//    private fun clickDebounce() : Boolean {
//        val current = isClickAllowed
//        if (isClickAllowed) {
//            isClickAllowed = false
//            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
//        }
//        return current
//    }

    inner class TrackViewHolder(private val binding: TrackBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(track: TrackParcelable) {
            binding.song.text = track.trackName
            binding.artistName.text = track.artistName
            binding.trackTime.text = formatTrackTime(track.trackTimeMillis)
            Glide.with(binding.cover.context).load(track.artworkUrl100)
                .placeholder(R.drawable.placeholder)
                .transform(RoundedCorners(16))
                .into(binding.cover)

            binding.root.setOnClickListener {
                onTrackClick(track)
            }
        }

    }
//    companion object {
//        private const val CLICK_DEBOUNCE_DELAY = 1000L
//    }
}
