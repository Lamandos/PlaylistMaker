package com.example.playlistmaker.media.ui.fragments

import PlaylistAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.media.ui.view_model.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {
    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlaylistsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        setupObservers()
    }

    private lateinit var adapter: PlaylistAdapter

    private fun setupViews() {
        binding.newPlaylist.setOnClickListener {
            requireParentFragment().findNavController().navigate(R.id.action_mediaFragment_to_playlistFragment)
        }

        adapter = PlaylistAdapter()
        binding.playlistRecycler.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.playlistRecycler.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.playlists.observe(viewLifecycleOwner) { list ->
            if (list.isEmpty()) {
                binding.playlistNoResultsImage.visibility = View.VISIBLE
                binding.playlistNoResultsMessage.visibility = View.VISIBLE
                binding.playlistRecycler.visibility = View.GONE
            } else {
                binding.playlistNoResultsImage.visibility = View.GONE
                binding.playlistNoResultsMessage.visibility = View.GONE
                binding.playlistRecycler.visibility = View.VISIBLE
                adapter.setItems(list)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    override fun onResume() {
        super.onResume()
        viewModel.loadPlaylists()
    }
    companion object {
        fun newInstance() = PlaylistsFragment()
    }
}
