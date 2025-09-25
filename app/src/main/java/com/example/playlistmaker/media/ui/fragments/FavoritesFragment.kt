package com.example.playlistmaker.media.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.TrackAdapter
import com.example.playlistmaker.databinding.FragmentFavoritesBinding
import com.example.playlistmaker.media.ui.view_model.FavoritesViewModel
import com.example.playlistmaker.search.domain.model.toParcelable
import com.example.playlistmaker.search.ui.TrackParcelable
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FavoritesViewModel by viewModel()

    private lateinit var adapter: TrackAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
    }

    private fun setupRecyclerView() {
        adapter = TrackAdapter(mutableListOf()) { track ->
            navigateToPlayer(track)
        }
        binding.favoritesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.favoritesRecyclerView.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.screenState.observe(viewLifecycleOwner) { state ->
            if (state.isEmpty) {
                binding.mediaNoResults.visibility = View.VISIBLE
                binding.favoritesRecyclerView.visibility = View.GONE
            } else {
                binding.mediaNoResults.visibility = View.GONE
                binding.favoritesRecyclerView.visibility = View.VISIBLE
                val trackParcelableList = state.favoriteTracks.map { it.toParcelable() }
                adapter.updateTracks(trackParcelableList)
            }
        }
    }

    private fun navigateToPlayer(track: TrackParcelable) {
        val bundle = Bundle().apply {
            putParcelable("track", track)
        }
        findNavController().navigate(R.id.action_global_playerFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = FavoritesFragment()
    }
}
