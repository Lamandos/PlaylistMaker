
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.playlist.ui.model.PlaylistUi

class PlaylistAdapter : RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>() {

    private val items = mutableListOf<PlaylistUi>()

    fun setItems(newItems: List<PlaylistUi>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_playlist, parent, false)
        return PlaylistViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class PlaylistViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val cover: ImageView = view.findViewById(R.id.playlistCover)
        private val name: TextView = view.findViewById(R.id.playlistName)
        private val trackCount: TextView = view.findViewById(R.id.trackCount)

        fun bind(item: PlaylistUi) {
            name.text = item.name
            trackCount.text = "${item.trackCount} ${getTrackWordForm(item.trackCount)}"

            if (!item.coverImagePath.isNullOrEmpty()) {
                Glide.with(itemView)
                    .load(item.coverImagePath)
                    .placeholder(R.drawable.placeholder)
                    .into(cover)
            } else {
                cover.setImageResource(R.drawable.placeholder)
            }
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
}
