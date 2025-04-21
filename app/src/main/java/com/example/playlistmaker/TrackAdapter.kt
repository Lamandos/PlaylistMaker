package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.databinding.ActivityMainBinding
import com.example.playlistmaker.databinding.TrackBinding

class TrackAdapter(private var tracks: MutableList<Track>) : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding = TrackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = tracks[position]
        holder.bind(track)
    }

    override fun getItemCount() = tracks.size

    fun updateTracks(newTracks: List<Track>) {
        tracks.clear()
        tracks.addAll(newTracks)
        notifyDataSetChanged()
    }

    fun clearTracks() {
        tracks.clear()
        notifyDataSetChanged()
    }

    inner class TrackViewHolder(private val binding: TrackBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(track: Track) {
            binding.song.text = track.trackName
            binding.artistName.text = track.artistName
            binding.trackTime.text = formatTrackTime(track.trackTimeMillis)
            Glide.with(binding.cover.context).load(track.artworkUrl100)
                .placeholder(R.drawable.placeholder)
                .into(binding.cover)
        }
    }

    private fun formatTrackTime(timeInMillis: Long): String {
        val minutes = timeInMillis / 60000
        val seconds = (timeInMillis % 60000) / 1000
        return String.format("%02d:%02d", minutes, seconds)
    }
}
