package com.example.playlistmaker.playlist.ui.fragments

import PlaylistViewModel
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.db.data.AppDatabasePlaylist
import com.example.playlistmaker.playlist.domain.PlaylistInteractor
import com.example.playlistmaker.playlist.domain.PlaylistRepository

class PlaylistFragment : Fragment() {

    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!
    private var selectedCoverImageUri: Uri? = null
    private lateinit var viewModel: PlaylistViewModel

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                selectedCoverImageUri = it
                binding.playlistImage.setImageURI(it)
                binding.playlistImage.visibility = View.VISIBLE
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = AppDatabasePlaylist.getDatabase(requireContext())
        val playlistDao = database.playlistDao()
        val playlistTrackDao = database.playlistTrackDao()
        val repository = PlaylistRepository(playlistDao, playlistTrackDao)
        val interactor = PlaylistInteractor(repository)

        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PlaylistViewModel(requireActivity().application, interactor) as T
            }
        }).get(PlaylistViewModel::class.java)

        viewModel.isPlaylistCreated.observe(viewLifecycleOwner) { success ->
            if (success) {
                val playlistName = viewModel.createdPlaylistName.value ?: "плейлист"
                Toast.makeText(requireContext(), "Плейлист $playlistName создан", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }

        binding.createButton.isEnabled = false
        binding.editName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.createButton.isEnabled = !s.isNullOrBlank()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.createButton.setOnClickListener {
            val name = binding.editName.text.toString().trim()
            val description = binding.editDescription.text.toString().trim()
            val coverImagePath = selectedCoverImageUri?.let { viewModel.saveCoverImage(it) } ?: ""
            viewModel.createPlaylist(name, description, coverImagePath)
        }

        binding.playlistBack.setOnClickListener {
            handleBack()
        }

        binding.playlistImageContainer.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        setButtonBottomPadding()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    handleBack()
                }
            }
        )
    }

    private fun handleBack() {
        if (hasUnsavedData()) {
            showExitDialog()
        } else {
            findNavController().popBackStack()
        }
    }

    private fun hasUnsavedData(): Boolean {
        return !binding.editName.text.isNullOrBlank() ||
                !binding.editDescription.text.isNullOrBlank() ||
                selectedCoverImageUri != null
    }

    private fun showExitDialog() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Завершить создание плейлиста?")
            .setMessage("Все несохраненные данные будут потеряны")
            .setPositiveButton("Завершить") { _, _ ->
                findNavController().popBackStack()
            }
            .setNegativeButton("Отмена", null)
        builder.create().show()
    }

    private fun setButtonBottomPadding() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val bottomInset = systemBarsInsets.bottom

            binding.bottomContainer.setPadding(
                binding.bottomContainer.paddingLeft,
                binding.bottomContainer.paddingTop,
                binding.bottomContainer.paddingRight,
                bottomInset
            )

            insets
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
