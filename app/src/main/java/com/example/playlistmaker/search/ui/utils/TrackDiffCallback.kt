package com.example.playlistmaker.search.ui.utils

import androidx.recyclerview.widget.DiffUtil
import com.example.playlistmaker.search.ui.TrackParcelable

class TrackDiffCallback(
    private val oldList: List<TrackParcelable>,
    private val newList: List<TrackParcelable>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].trackName == newList[newItemPosition].trackName &&
                oldList[oldItemPosition].artistName == newList[newItemPosition].artistName
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}