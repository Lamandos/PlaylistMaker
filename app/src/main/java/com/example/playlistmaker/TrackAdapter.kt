package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.TrackBinding
import com.example.playlistmaker.search.ui.TrackParcelable
import com.example.playlistmaker.search.ui.utils.TrackDiffCallback
import com.example.playlistmaker.search.ui.TrackViewHolder

class TrackAdapter(
    private var tracks: MutableList<TrackParcelable>,
    private val onTrackClick: (TrackParcelable) -> Unit
) : RecyclerView.Adapter<TrackViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding = TrackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackViewHolder(binding, onTrackClick)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = tracks[position]
        holder.bind(track)
    }

    override fun getItemCount(): Int = tracks.size

    fun updateTracks(newTracks: List<TrackParcelable>) {
        val diffCallback = TrackDiffCallback(tracks, newTracks)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        tracks.clear()
        tracks.addAll(newTracks)
        diffResult.dispatchUpdatesTo(this)
    }
}