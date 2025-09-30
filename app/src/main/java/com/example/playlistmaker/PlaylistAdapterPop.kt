package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.playlist.domain.Playlist

class PlaylistAdapterPop (
    private val onClick: (Playlist) -> Unit
) : RecyclerView.Adapter<PlaylistAdapterPop.PlaylistViewHolder>() {

    private val items = mutableListOf<Playlist>()

    fun setItems(newItems: List<Playlist>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_playlist_pop, parent, false)
        return PlaylistViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class PlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cover: ImageView = itemView.findViewById(R.id.playlistCover_pop)
        private val name: TextView = itemView.findViewById(R.id.playlistName_pop)
        private val count: TextView = itemView.findViewById(R.id.trackCountPop)

        fun bind(item: Playlist) {
            name.text = item.name
            count.text = "${item.trackCount} ${getTrackWordForm(item.trackCount)}"

            if (!item.coverImagePath.isNullOrEmpty()) {
                Glide.with(itemView)
                    .load(item.coverImagePath)
                    .placeholder(R.drawable.placeholder)
                    .into(cover)
            } else {
                cover.setImageResource(R.drawable.placeholder)
            }

            itemView.setOnClickListener { onClick(item) }
        }

        private fun getTrackWordForm(count: Int): String {
            val rem100 = count % 100
            val rem10 = count % 10
            return when {
                rem100 in 11..14 -> "треков"
                rem10 == 1 -> "трек"
                rem10 in 2..4 -> "трека"
                else -> "треков"
            }
        }
    }
    fun updateTrackCount(playlistId: Long) {
        val index = items.indexOfFirst { it.id == playlistId }
        if (index != -1) {
            val item = items[index]
            val updatedItem = item.copy(trackCount = item.trackCount + 1)
            items[index] = updatedItem
            notifyItemChanged(index)
        }
    }
}