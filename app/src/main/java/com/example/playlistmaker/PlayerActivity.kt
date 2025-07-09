package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class PlayerActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var playButton: ImageButton
    private lateinit var progressTextView: TextView
    private lateinit var progressHandler: Handler
    private var currentTrack: Track? = null
    private var isPlaying = false
    private var playbackPosition = 0
    private var isPrepared = false

    private val progressRunnable = object : Runnable {
        override fun run() {
            if (isPlaying && isPrepared) {
                val currentPosition = mediaPlayer.currentPosition
                progressTextView.text = formatTrackTime(currentPosition.toLong())
                progressHandler.postDelayed(this, 300)
            }
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        enableEdgeToEdge()
        setContentView(R.layout.activity_player)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.player)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        currentTrack = intent.getParcelableExtra("track")
        if (currentTrack == null) {
            finish()
            return
        }

        initMediaPlayer()
        initViews()
        setupTrackInfo()
        setupPlaybackControl()
        prepareMediaPlayer()
    }

    private fun prepareMediaPlayer() {
        currentTrack?.previewUrl?.let { url ->
            try {
                mediaPlayer.reset()
                mediaPlayer.setDataSource(url)
                mediaPlayer.prepareAsync()
            } catch (e: Exception) {
            }
        }
    }

    private fun initMediaPlayer() {
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setOnPreparedListener {
                isPrepared = true
                it.seekTo(playbackPosition)
            }
            setOnCompletionListener {
                playbackPosition = 0
                this@PlayerActivity.isPlaying = false
                progressTextView.text = formatTrackTime(0)
                updatePlayButton()
                progressHandler.removeCallbacks(progressRunnable)
            }
        }
    }

    private fun initViews() {
        progressHandler = Handler(Looper.getMainLooper())
        playButton = findViewById(R.id.playButton)
        progressTextView = findViewById(R.id.timer)

        findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            finish()
        }
    }

    private fun setupTrackInfo() {
        currentTrack?.let { track ->
            findViewById<TextView>(R.id.trackTitle).text = track.trackName
            findViewById<TextView>(R.id.artistName).text = track.artistName
            findViewById<TextView>(R.id.valueDuration).text = formatTrackTime(track.trackTimeMillis)
            progressTextView.text = formatTrackTime(0)

            track.collectionName?.let { albumName ->
                findViewById<TextView>(R.id.valueAlbum).text = albumName
            } ?: run {
                findViewById<TextView>(R.id.valueAlbum).visibility = View.GONE
            }

            findViewById<TextView>(R.id.valueYear).text = track.releaseDate?.take(4) ?: ""
            findViewById<TextView>(R.id.valueGenre).text = track.primaryGenreName
            findViewById<TextView>(R.id.valueCountry).text = track.country

            Glide.with(this)
                .load(track.getCoverArtwork())
                .placeholder(R.drawable.placeholder)
                .transform(RoundedCorners(dpToPx(16)))
                .into(findViewById(R.id.albumCover))
        }
    }

    private fun setupPlaybackControl() {
        playButton.setOnClickListener {
            if (isPlaying) {
                pausePlayback()
            } else {
                startPlayback()
            }
        }
    }

    private fun startPlayback() {
        if (isPrepared) {
            if (playbackPosition >= mediaPlayer.duration) {
                playbackPosition = 0
                mediaPlayer.seekTo(0)
            }
            mediaPlayer.start()
            isPlaying = true
            updatePlayButton()
            progressHandler.post(progressRunnable)
        }
    }

    private fun pausePlayback() {
        if (isPrepared) {
            mediaPlayer.pause()
            playbackPosition = mediaPlayer.currentPosition
        }
        isPlaying = false
        updatePlayButton()
        progressHandler.removeCallbacks(progressRunnable)
    }

    private fun stopPlayback(resetPosition: Boolean = false) {
        if (isPrepared) {
            mediaPlayer.stop()
            if (resetPosition) {
                playbackPosition = 0
                progressTextView.text = formatTrackTime(0)
            }
        }
        isPlaying = false
        isPrepared = false
        updatePlayButton()
        progressHandler.removeCallbacks(progressRunnable)
    }

    private fun updatePlayButton() {
        playButton.setImageResource(
            if (isPlaying) R.drawable.pause_btn else R.drawable.play_btn
        )
    }

    private fun formatTrackTime(timeInMillis: Long): String {
        val minutes = timeInMillis / 60000
        val seconds = (timeInMillis % 60000) / 1000
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun Context.dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    override fun onPause() {
        super.onPause()
        if (isPlaying) {
            pausePlayback()
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopPlayback()
        mediaPlayer.release()
        progressHandler.removeCallbacksAndMessages(null)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("track", currentTrack)
        outState.putInt("playbackPosition", playbackPosition)
        outState.putBoolean("isPlaying", isPlaying)
        outState.putBoolean("isPrepared", isPrepared)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currentTrack = savedInstanceState.getParcelable("track")
        playbackPosition = savedInstanceState.getInt("playbackPosition", 0)
        isPlaying = savedInstanceState.getBoolean("isPlaying", false)
        isPrepared = savedInstanceState.getBoolean("isPrepared", false)
    }
}