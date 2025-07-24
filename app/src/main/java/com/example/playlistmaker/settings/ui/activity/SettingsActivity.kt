package com.example.playlistmaker.settings.ui.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.sharing.domain.model.SharingInteractor
import com.example.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import com.example.playlistmaker.sharing.domain.model.createSupportEmail

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val sharingInteractor: SharingInteractor = Creator.provideSharingInteractor()

    private val settingsViewModel: SettingsViewModel by viewModels {
        Creator.provideSettingsViewModelFactory(this)
    }

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
            sharingInteractor.openTerms(this, getString(R.string.agreement_text))
        }

        binding.agreementText.setOnClickListener {
            sharingInteractor.openTerms(this, getString(R.string.agreement_text))
        }

        binding.share.setOnClickListener {
            sharingInteractor.shareApp(this, getString(R.string.share_text))
        }

        binding.shareText.setOnClickListener {
            sharingInteractor.shareApp(this, getString(R.string.share_text))
        }

        binding.support.setOnClickListener {
            sharingInteractor.openSupport(this@SettingsActivity, createSupportEmail())
        }

        binding.supportText.setOnClickListener {
            sharingInteractor.openSupport(this@SettingsActivity, createSupportEmail())
        }
    }

    private fun updateThemeSwitch(isDarkMode: Boolean) {
        val themeSwitch: SwitchCompat = binding.themeSwitch
        themeSwitch.isChecked = isDarkMode
    }
}
