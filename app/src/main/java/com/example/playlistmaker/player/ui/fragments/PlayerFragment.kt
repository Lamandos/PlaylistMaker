package com.example.playlistmaker.player.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.player.PlayerState
import com.example.playlistmaker.player.ui.view_model.PlayerScreenState
import com.example.playlistmaker.player.ui.view_model.PlayerViewModel
import com.example.playlistmaker.search.ui.TrackParcelable
import com.example.playlistmaker.utils.dpToPx
import com.example.playlistmaker.utils.formatTrackTime
import com.example.playlistmaker.utils.view.ImageUtils
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerFragment : Fragment() {

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlayerViewModel by viewModel()
    private lateinit var track: TrackParcelable

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        track = arguments?.getParcelable(TRACK_EXTRA) ?: run {
            findNavController().popBackStack()
            return
        }

        setupUI(track)
        initObservers()
        initListeners()

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack()
                }
            }
        )

        viewModel.preparePlayer(track.previewUrl, track.trackName, track.artistName)
    }

    private fun setupUI(track: TrackParcelable) = with(binding) {
        trackTitle.text = track.trackName
        artistName.text = track.artistName
        valueDuration.text = formatTrackTime(track.trackTimeMillis)
        valueYear.text = track.releaseDate?.take(4).orEmpty()
        valueGenre.text = track.primaryGenreName
        valueCountry.text = track.country
        playButton.isEnabled = true
        timer.text = formatTrackTime(0)

        if (track.collectionName.isNullOrEmpty()) {
            valueAlbum.visibility = View.GONE
        } else {
            valueAlbum.text = track.collectionName
        }

        Glide.with(this@PlayerFragment)
            .load(ImageUtils.getCoverArtwork(track.artworkUrl100))
            .placeholder(R.drawable.placeholder)
            .transform(RoundedCorners(requireContext().dpToPx(16)))
            .into(albumCover)
    }

    private fun initObservers() {
        viewModel.screenState.observe(viewLifecycleOwner) { state ->
            state?.let { renderState(it) }
        }
    }

    private fun renderState(state: PlayerScreenState) = with(binding) {
        playButton.setImageResource(
            when (state.playerState) {
                PlayerState.Playing -> R.drawable.pause_btn
                else -> R.drawable.play_btn
            }
        )
        timer.text = state.currentTime
    }

    private fun initListeners() = with(binding) {
        playButton.setOnClickListener { viewModel.togglePlayback() }
        backButton.setOnClickListener { findNavController().popBackStack() }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.release()
        _binding = null
    }

    companion object {
        const val TRACK_EXTRA = "track"

        fun newInstance(track: TrackParcelable): PlayerFragment {
            return PlayerFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(TRACK_EXTRA, track)
                }
            }
        }
    }
}
