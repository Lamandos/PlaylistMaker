package com.example.playlistmaker.playlist_tracks.ui.fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.TrackAdapter
import com.example.playlistmaker.databinding.FragmentPlaylistTracksBinding
import com.example.playlistmaker.playlist.domain.Playlist
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.example.playlistmaker.playlist_tracks.ui.view_model.PlaylistTracksViewModel
import com.example.playlistmaker.search.domain.model.toParcelable
import com.example.playlistmaker.search.ui.TrackParcelable
import kotlin.math.roundToInt

class PlaylistTracksFragment : Fragment() {

    private var _binding: FragmentPlaylistTracksBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaylistTracksViewModel by viewModel()
    private lateinit var trackAdapter: TrackAdapter

    private var playlistId: Long = -1L
    private lateinit var tracksBottomSheetBehavior: BottomSheetBehavior<FrameLayout>
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<android.widget.LinearLayout>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.statusBarColor = resources.getColor(R.color.yp_light_gray, null)

        playlistId = arguments?.getLong(ARG_PLAYLIST_ID) ?: -1L
        if (playlistId == -1L) {
            findNavController().navigateUp()
            return
        }

        setupTracksBottomSheet()
        setupRecycler()
        setupBottomSheet()
        setupListeners()
        observeViewModel()
        viewModel.loadPlaylist(playlistId)
    }
    private fun setupTracksBottomSheet() {
        tracksBottomSheetBehavior = BottomSheetBehavior.from(binding.tracksBottomSheet).apply {
            isHideable = false
            state = BottomSheetBehavior.STATE_COLLAPSED
            skipCollapsed = false
        }

        tracksBottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING || newState == BottomSheetBehavior.STATE_SETTLING) {
                    binding.playlistRecyclerView.isClickable = true
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.playlistRecyclerView.isClickable = true
            }
        })
    }

    private fun setupRecycler() {
        applyBottomInset()
        trackAdapter = TrackAdapter(
            mutableListOf(),
            onTrackClick = { track ->
                val direction = PlaylistTracksFragmentDirections
                    .actionPlaylistTracksFragmentToPlayerFragment(track)
                findNavController().navigate(direction)
            },
            onTrackLongClick = { track ->
                showDeleteTrackDialog(track)
            }
        )
        binding.playlistRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.playlistRecyclerView.adapter = trackAdapter
    }
    private fun applyBottomInset() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.playlistRecyclerView) { view, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val bottomInset = systemBarsInsets.bottom

            binding.playlistRecyclerView.setPadding(
                view.paddingLeft,
                view.paddingTop,
                view.paddingRight,
                bottomInset
            )
            insets
        }
    }
    private fun setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistMenu).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
            isHideable = true

            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    binding.overlay.visibility =
                        if (newState == BottomSheetBehavior.STATE_HIDDEN) View.GONE else View.VISIBLE
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    val normalizedOffset = (slideOffset + 1) / 2
                    binding.overlay.alpha = normalizedOffset
                }
            })
        }
    }

    private fun setupListeners() {
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.share.setOnClickListener {
            sharePlaylist()
        }

        binding.more.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        binding.playlistShare.setOnClickListener {
            sharePlaylist()
        }

        binding.playlistEdit.setOnClickListener {

            val direction = PlaylistTracksFragmentDirections.actionPlaylistTracksFragmentToEditPlaylistFragment(playlistId)
            findNavController().navigate(direction)

        }

        binding.playlistDelete.setOnClickListener {
            showDeletePlaylistDialog()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun showPlaylistItemInBottomSheet(playlist: Playlist) {
        val playlistPopContainer = binding.playlistPopContainer

        val playlistItemPopView = layoutInflater.inflate(R.layout.item_playlist_pop, null)

        val playlistCover = playlistItemPopView.findViewById<ImageView>(R.id.playlistCover_pop)
        val playlistName = playlistItemPopView.findViewById<TextView>(R.id.playlistName_pop)
        val trackCount = playlistItemPopView.findViewById<TextView>(R.id.trackCountPop)

        playlistName.text = playlist.name
        trackCount.text = "${playlist.trackCount} ${getTrackWordForm(playlist.trackCount)}"

        Glide.with(requireContext())
            .load(playlist.coverImagePath)
            .placeholder(R.drawable.placeholder)
            .into(playlistCover)

        playlistPopContainer.removeAllViews()
        playlistPopContainer.addView(playlistItemPopView)
    }

    private fun sharePlaylist() {
        val shareText = viewModel.buildShareText()
        if (shareText == null) {
            Toast.makeText(requireContext(), "В этом плейлисте нет списка треков, которым можно поделиться", Toast.LENGTH_LONG).show()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        } else {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
            }
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            startActivity(Intent.createChooser(shareIntent, "Поделиться плейлистом"))
        }
    }

    private fun showDeletePlaylistDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Удалить плейлист")
            .setMessage("Вы уверены, что хотите удалить этот плейлист?")
            .setPositiveButton("Удалить") { _, _ ->

                viewModel.deletePlaylist()

                viewModel.deleteStatus.observe(viewLifecycleOwner) { isDeleted ->
                    if (isDeleted) {
                        findNavController().popBackStack()
                    }
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }


    private fun observeViewModel() {
        viewModel.playlist.observe(viewLifecycleOwner) { playlist ->
            playlist?.let { renderPlaylist(it) }
        }

        viewModel.tracks.observe(viewLifecycleOwner) { tracks ->
            val parcelableTracks = tracks.map { it.toParcelable() }
            trackAdapter.updateTracks(parcelableTracks)

            binding.tracksTotal.text = "${tracks.size} ${getTrackWordForm(tracks.size)}"
            val totalDurationMs = tracks.sumOf { it.trackTimeMillis }
            val totalMinutes = (totalDurationMs / 60000.0).roundToInt()
            binding.playlistTotalTime.text = "$totalMinutes мин"

            if (tracks.isEmpty()) {
                binding.playlistRecyclerView.visibility = View.GONE
                binding.tracksNoResultsImage.visibility = View.VISIBLE
                binding.tracksNoResultsMessage.visibility = View.VISIBLE
            } else {
                binding.playlistRecyclerView.visibility = View.VISIBLE
                binding.tracksNoResultsImage.visibility = View.GONE
                binding.tracksNoResultsMessage.visibility = View.GONE
            }

        }
    }

    private fun renderPlaylist(playlist: Playlist) {
        binding.playlistTitle.text = playlist.name
        binding.playlistDescription.text = playlist.description ?: ""

        playlist.coverImagePath?.let { path ->
            if (path.isNotBlank()) {
                Glide.with(requireContext())
                    .load(path)
                    .placeholder(R.drawable.placeholder)
                    .into(binding.playlistCover)
            } else {
                binding.playlistCover.setImageResource(R.drawable.placeholder)
            }
        } ?: binding.playlistCover.setImageResource(R.drawable.placeholder)

        showPlaylistItemInBottomSheet(playlist)
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

    private fun showDeleteTrackDialog(track: TrackParcelable) {
        AlertDialog.Builder(requireContext())
            .setMessage("Хотите удалить трек?")
            .setPositiveButton("Да") { _, _ ->
                viewModel.deleteTrackFromPlaylist(track.id)
            }
            .setNegativeButton("Нет", null)
            .show()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().window.statusBarColor = resources.getColor(R.color.default_status_bar, null)
        _binding = null
    }

    companion object {
        const val ARG_PLAYLIST_ID = "playlistId"

        fun newInstance(playlistId: Long) = PlaylistTracksFragment().apply {
            arguments = Bundle().apply {
                putLong(ARG_PLAYLIST_ID, playlistId)
            }
        }
    }
}
