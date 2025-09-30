package com.example.playlistmaker.player.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.PlaylistAdapterPop
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.player.PlayerState
import com.example.playlistmaker.player.ui.view_model.PlayerScreenState
import com.example.playlistmaker.player.ui.view_model.PlayerViewModel
import com.example.playlistmaker.playlist.domain.Playlist
import com.example.playlistmaker.search.domain.model.toTrack
import com.example.playlistmaker.search.ui.TrackParcelable
import com.example.playlistmaker.utils.dpToPx
import com.example.playlistmaker.utils.formatTrackTime
import com.example.playlistmaker.utils.view.ImageUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerFragment : Fragment() {

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlayerViewModel by viewModel()
    private lateinit var track: TrackParcelable

    private lateinit var playlistAdapterPop: PlaylistAdapterPop
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>

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

        val bottomSheetContainer = binding.root.findViewById<LinearLayout>(R.id.playlists_bottom_sheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN // скрыто по умолчанию

        binding.createNewPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.action_playerFragment_to_playlistFragment)
        }

        ViewCompat.setOnApplyWindowInsetsListener(bottomSheetContainer) { view, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                view.paddingLeft,
                view.paddingTop,
                view.paddingRight,
                systemBarsInsets.bottom
            )
            insets
        }

        val overlay = binding.root.findViewById<View>(R.id.overlay)

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        overlay.visibility = View.GONE
                    }
                    else -> {
                        overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                val normalizedOffset = (slideOffset + 1) / 2
                overlay.alpha = normalizedOffset
            }
        })

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack()
                }
            }
        )

        viewModel.preparePlayer(track.previewUrl, track.trackName, track.artistName, track.toTrack())
        setupPlaylistBottomSheet()
        viewModel.loadPlaylists()
    }

    private var currentPlaylists: List<Playlist> = emptyList()

    private fun setupPlaylistBottomSheet() = with(binding) {
        playlistAdapterPop = PlaylistAdapterPop { playlist ->

            viewModel.addTrackToPlaylist(track.toTrack(), playlist)
        }

        viewModel.trackAddStatus.observe(viewLifecycleOwner) { (playlistId, success) ->

            val playlist = currentPlaylists.find { it.id == playlistId }

            val playlistName = playlist?.name ?: "плейлист"

            if (success) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                Toast.makeText(requireContext(), "Добавлено в плейлист $playlistName", Toast.LENGTH_SHORT).show()

                playlistAdapterPop.updateTrackCount(playlistId)
            } else {
                Toast.makeText(requireContext(), "Трек уже добавлен в плейлист $playlistName", Toast.LENGTH_SHORT).show()
            }
        }

        playlistRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        playlistRecyclerView.adapter = playlistAdapterPop

        viewModel.playlists.observe(viewLifecycleOwner) { playlists ->

            currentPlaylists = playlists

            if (playlists.isEmpty()) {
                playlistRecyclerView.visibility = View.GONE
                playlistNoResultsImagePop.visibility = View.VISIBLE
                playlistNoResultsMessagePop.visibility = View.VISIBLE
            } else {
                playlistRecyclerView.visibility = View.VISIBLE
                playlistNoResultsImagePop.visibility = View.GONE
                playlistNoResultsMessagePop.visibility = View.GONE

                playlistAdapterPop.setItems(playlists)
            }
        }
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

        updateFavoriteButton(track.isFavorite)
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
        updateFavoriteButton(state.isFavorite)
    }

    private fun updateFavoriteButton(isFavorite: Boolean) {
        val imageRes = if (isFavorite) {
            R.drawable.heart_btn_fill
        } else {
            R.drawable.heart_btn
        }

        binding.likeButton.setImageResource(imageRes)
    }

    private fun initListeners() = with(binding) {
        playButton.setOnClickListener { viewModel.togglePlayback() }
        backButton.setOnClickListener { findNavController().popBackStack() }
        likeButton.setOnClickListener {
            likeButton.isEnabled = false
            viewModel.onFavoriteClicked()
            likeButton.postDelayed({ likeButton.isEnabled = true }, 300)
        }

        addButton.setOnClickListener {
            if (::bottomSheetBehavior.isInitialized) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
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


