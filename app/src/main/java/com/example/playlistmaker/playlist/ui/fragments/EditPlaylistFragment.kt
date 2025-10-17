package com.example.playlistmaker.playlist.ui.fragments

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.db.data.PlaylistEntity
import com.example.playlistmaker.playlist.ui.view_model.EditPlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class EditPlaylistFragment : PlaylistFragment() {

    private val editPlaylistViewModel: EditPlaylistViewModel by viewModel()
    private var playlistId: Long = -1L
    private var originalCoverImagePath: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlistId = arguments?.getLong("playlistId", -1L) ?: -1L

        if (playlistId != -1L) {
            editPlaylistViewModel.loadPlaylist(playlistId)
        }

        editPlaylistViewModel.playlist.observe(viewLifecycleOwner) { playlist ->
            playlist?.let {
                populateFieldsWithPlaylistData(it)
            }
        }

        binding.createButton.setOnClickListener {
            savePlaylistChanges()
        }

        binding.headerTitle.text = "Редактировать"
        binding.createButton.text = "Сохранить"
    }

    private fun populateFieldsWithPlaylistData(playlist: PlaylistEntity) {
        binding.editName.setText(playlist.name)
        binding.editDescription.setText(playlist.description ?: "")

        originalCoverImagePath = playlist.coverImagePath?: ""

        playlist.coverImagePath?.let {
            loadCoverImage(it)
        }
    }

    private fun loadCoverImage(imagePath: String) {
        try {
            if (imagePath.startsWith("content://")) {
                val uri = Uri.parse(imagePath)
                binding.playlistImage.setImageURI(uri)
                selectedCoverImageUri = uri
            } else {
                val file = File(imagePath)
                if (file.exists()) {
                    val uri = Uri.fromFile(file)
                    binding.playlistImage.setImageURI(uri)
                    selectedCoverImageUri = uri
                } else {
                    binding.playlistImage.visibility = View.GONE
                    return
                }
            }
            binding.playlistImage.visibility = View.VISIBLE
        } catch (e: Exception) {
            e.printStackTrace()
            binding.playlistImage.visibility = View.GONE
        }
    }

    private fun savePlaylistChanges() {
        val name = binding.editName.text.toString().trim()
        val description = binding.editDescription.text.toString().trim()

        if (name.isEmpty()) {
            Toast.makeText(requireContext(), "Введите название плейлиста", Toast.LENGTH_SHORT).show()
            return
        }

        val coverImagePath = if (selectedCoverImageUri != null && isNewImageSelected()) {
            editPlaylistViewModel.saveCoverImage(selectedCoverImageUri!!)
        } else {
            originalCoverImagePath
        }

        val updatedPlaylist = PlaylistEntity(
            id = playlistId,
            name = name,
            description = description,
            coverImagePath = coverImagePath,
            trackCount = editPlaylistViewModel.playlist.value?.trackCount ?: 0
        )

        editPlaylistViewModel.updatePlaylist(updatedPlaylist)
        Toast.makeText(requireContext(), "Плейлист обновлен", Toast.LENGTH_SHORT).show()
        findNavController().popBackStack()
    }

    private fun isNewImageSelected(): Boolean {
        return selectedCoverImageUri?.toString()?.startsWith("content://") == true
    }

    override fun handleBack() {
         findNavController().popBackStack()
    }


    companion object {
        fun newInstance(playlistId: Long): EditPlaylistFragment {
            val fragment = EditPlaylistFragment()
            val args = Bundle()
            args.putLong("playlistId", playlistId)
            fragment.arguments = args
            return fragment
        }
    }
}