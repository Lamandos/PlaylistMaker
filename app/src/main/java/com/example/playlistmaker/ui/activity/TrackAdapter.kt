package com.example.playlistmaker.ui.activity

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.TrackBinding
import com.example.playlistmaker.presentation.TrackParcelable
import com.example.playlistmaker.ui.utils.TrackDiffCallback
import com.example.playlistmaker.ui.view.TrackViewHolder

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
        val diffResult = androidx.recyclerview.widget.DiffUtil.calculateDiff(diffCallback)

        tracks.clear()
        tracks.addAll(newTracks)
        diffResult.dispatchUpdatesTo(this)
    }
}
