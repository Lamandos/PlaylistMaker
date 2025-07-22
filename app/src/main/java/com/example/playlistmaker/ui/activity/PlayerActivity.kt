package com.example.playlistmaker.ui.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.presentation.TrackParcelable
import com.example.playlistmaker.presentation.state.PlayerState
import com.example.playlistmaker.presentation.viewmodel.PlayerViewModel
import com.example.playlistmaker.ui.utils.dpToPx
import com.example.playlistmaker.ui.utils.formatTrackTime
import com.example.playlistmaker.ui.view.ImageUtils

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private val viewModel: PlayerViewModel by viewModels {
        Creator.providePlayerViewModelFactory()
    }

    private lateinit var track: TrackParcelable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(binding.player) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        track = intent.getParcelableExtra(TRACK_EXTRA) ?: run {
            finish()
            return
        }

        setupUI(track)
        initObservers()
        initListeners()

        viewModel.preparePlayer(track.previewUrl)
    }

    private fun setupUI(track: TrackParcelable) = with(binding) {
        trackTitle.text = track.trackName
        artistName.text = track.artistName
        valueDuration.text = formatTrackTime(track.trackTimeMillis)
        valueYear.text = track.releaseDate?.take(4).orEmpty()
        valueGenre.text = track.primaryGenreName
        valueCountry.text = track.country
        timer.text = formatTrackTime(0)

        if (track.collectionName.isNullOrEmpty()) {
            valueAlbum.visibility = android.view.View.GONE
        } else {
            valueAlbum.text = track.collectionName
        }

        Glide.with(this@PlayerActivity)
            .load(ImageUtils.getCoverArtwork(track.artworkUrl100))
            .placeholder(R.drawable.placeholder)
            .transform(RoundedCorners(dpToPx(16)))
            .into(albumCover)
    }

    private fun initObservers() {
        viewModel.playerState.observe(this) { state ->
            binding.playButton.setImageResource(
                when (state) {
                    is PlayerState.Playing -> R.drawable.pause_btn
                    else -> R.drawable.play_btn
                }
            )
        }

        viewModel.currentTime.observe(this) { time ->
            binding.timer.text = time
        }
    }

    private fun initListeners() = with(binding) {
        playButton.setOnClickListener {
            viewModel.togglePlayback()
        }
        backButton.setOnClickListener {
            finish()
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.release()
    }

    companion object {
        const val TRACK_EXTRA = "track"
    }
}
