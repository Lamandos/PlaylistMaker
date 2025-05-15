package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import android.view.View

class PlayerActivity  : AppCompatActivity() {

    private var currentTrack: Track? = null


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_player)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.player)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val track: Track? = intent.getParcelableExtra("track")

        track?.let {
            val trackTitleTextView: TextView = findViewById(R.id.trackTitle)
            val artistTextView: TextView = findViewById(R.id.artistName)
            val albumCoverImageView: ImageView = findViewById(R.id.albumCover)
            val trackTimeTextView: TextView = findViewById(R.id.timer)
            val albumTextView: TextView = findViewById(R.id.valueAlbum)
            val yearTextView: TextView = findViewById(R.id.valueYear)
            val genreTextView: TextView = findViewById(R.id.valueGenre)
            val countryTextView: TextView = findViewById(R.id.valueCountry)
            val valueDuration: TextView = findViewById(R.id.valueDuration)

            trackTitleTextView.text = it.trackName
            artistTextView.text = it.artistName
            trackTimeTextView.text = formatTrackTime(it.trackTimeMillis)
            yearTextView.text = it.releaseDate?.substring(0, 4)
            genreTextView.text = it.primaryGenreName
            countryTextView.text = it.country
            valueDuration.text = formatTrackTime(it.trackTimeMillis)

            if (track.collectionName != null) {
                albumTextView.text = track.collectionName
            } else {
                albumTextView.visibility = View.GONE
            }
            fun Context.dpToPx(dp: Int): Int {
                return (dp * resources.displayMetrics.density).toInt()
            }
            Glide.with(this)
                .load(it.getCoverArtwork())
                .placeholder(R.drawable.placeholder)
                .transform(RoundedCorners(dpToPx(16)))
                .into(albumCoverImageView)

            val backButton = findViewById<ImageButton>(R.id.backButton)
            backButton.setOnClickListener {
                finish()
            }
        }
    }

    private fun formatTrackTime(timeInMillis: Long): String {
        val minutes = timeInMillis / 60000
        val seconds = (timeInMillis % 60000) / 1000
        return String.format("%02d:%02d", minutes, seconds)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currentTrack = savedInstanceState.getParcelable("track")
        currentTrack?.let { track ->
            val trackTitleTextView: TextView = findViewById(R.id.trackTitle)
            trackTitleTextView.text = track.trackName
        }
    }
}

