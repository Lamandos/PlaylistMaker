package com.example.playlistmaker.settings.ui.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val settingsViewModel: SettingsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        binding.themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.toggleTheme(isChecked)
        }

        setClickListeners()
    }

    private fun setClickListeners() {
        listOf(
            binding.agreement to settingsViewModel::openTerms,
            binding.agreementText to settingsViewModel::openTerms,
            binding.share to settingsViewModel::shareApp,
            binding.shareText to settingsViewModel::shareApp,
            binding.support to settingsViewModel::openSupport,
            binding.supportText to settingsViewModel::openSupport
        ).forEach { (view, action) ->
            view.setOnClickListener { action(requireContext()) }
        }
    }

    private fun observeViewModel() {
        settingsViewModel.isDarkModeEnabled.observe(viewLifecycleOwner) { isDarkMode ->
            updateThemeSwitch(isDarkMode)
        }
    }

    private fun updateThemeSwitch(isDarkMode: Boolean) {
        binding.themeSwitch.isChecked = isDarkMode
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = SettingsFragment()
    }
}
