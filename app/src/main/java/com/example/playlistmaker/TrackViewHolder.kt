package com.example.playlistmaker

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions


fun dpToPx(context: Context, dp: Float): Int {
    return (dp * context.resources.displayMetrics.density).toInt()
}

class TrackViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.track, parent, false)
) {
    private val trackName: TextView = itemView.findViewById(R.id.song)
    private val artistName: TextView = itemView.findViewById(R.id.artist_name)
    private val trackTime: TextView = itemView.findViewById(R.id.track_time)
    private val coverImage: ImageView = itemView.findViewById(R.id.cover)

    fun bind(track: Track) {
        trackName.text = track.trackName
        artistName.text = track.artistName
        trackTime.text = track.trackTime


        val context = itemView.context


        val cornerRadiusDp = 2f
        val cornerRadiusPx = dpToPx(context, cornerRadiusDp)


        Glide.with(itemView.context)
            .load(track.artworkUrl100)
            .transform(CenterCrop(), RoundedCorners(cornerRadiusPx))
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder)
            .fallback(R.drawable.placeholder)
            .into(coverImage)

    }
}
