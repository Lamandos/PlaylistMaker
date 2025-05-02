import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.Track
import com.google.gson.Gson

class SearchHistory(private val sharedPreferences: SharedPreferences) {

    fun getSearchHistory(): List<Track> {
        val historyJson = sharedPreferences.getString("history", "[]") ?: "[]"
        return Gson().fromJson(historyJson, Array<Track>::class.java).toList()
    }

    fun saveSearchHistory(history: List<Track>) {
        val historyJson = Gson().toJson(history)
        sharedPreferences.edit().putString("history", historyJson).apply()
    }

    fun clearSearchHistory() {
        sharedPreferences.edit().remove("history").apply()
    }

    fun addToSearchHistory(track: Track) {
        val history = getSearchHistory().toMutableList()

        history.removeIf { it.trackName == track.trackName && it.artistName == track.artistName && it.trackTimeMillis == track.trackTimeMillis}

        history.add(0, track)

        if (history.size > 10) {
            history.removeAt(history.size - 1)
        }

        saveSearchHistory(history)
    }
}
