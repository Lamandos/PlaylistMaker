package com.example.playlistmaker.settings.ui.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val settingsViewModel: SettingsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupUI()

        settingsViewModel.isDarkModeEnabled.observe(this) { isDarkMode ->
            updateThemeSwitch(isDarkMode)
        }
    }

    private fun setupUI() {
        binding.settingsBtnBack.setOnClickListener { finish() }

        val themeSwitch: SwitchCompat = binding.themeSwitch
        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.toggleTheme(isChecked)
        }

        binding.agreement.setOnClickListener {
            settingsViewModel.openTerms(this)
        }

        binding.agreementText.setOnClickListener {
            settingsViewModel.openTerms(this)
        }

        binding.share.setOnClickListener {
            settingsViewModel.shareApp(this)
        }

        binding.shareText.setOnClickListener {
            settingsViewModel.shareApp(this)
        }

        binding.support.setOnClickListener {
            settingsViewModel.openSupport(this)
        }

        binding.supportText.setOnClickListener {
            settingsViewModel.openSupport(this)
        }
    }

    private fun updateThemeSwitch(isDarkMode: Boolean) {
        val themeSwitch: SwitchCompat = binding.themeSwitch
        themeSwitch.isChecked = isDarkMode
    }
}
