package com.example.playlistmaker.ui.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.domain.interactor.ThemeInteractor


class SettingsActivity : AppCompatActivity() {
    private lateinit var themeInteractor: ThemeInteractor
    private lateinit var binding: ActivitySettingsBinding
    private val emailService = Creator.provideEmailService()
    private val shareService = Creator.provideShareService()
    private val userAgreementService = Creator.provideUserAgreementService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        themeInteractor = Creator.provideThemeInteractor(this)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(binding.settings) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.settingsBtnBack.setOnClickListener { finish() }

        binding.agreement.setOnClickListener {
            userAgreementService.openUserAgreement(this, getString(R.string.agreement_text))
        }
        binding.agreementText.setOnClickListener {
            userAgreementService.openUserAgreement(this, getString(R.string.agreement_text))
        }

        binding.share.setOnClickListener {
            shareService.shareApp(this, getString(R.string.share_text))
        }
        binding.shareText.setOnClickListener {
            shareService.shareApp(this, getString(R.string.share_text))
        }

        binding.support.setOnClickListener {
            sendEmail()
        }
        binding.supportText.setOnClickListener {
            sendEmail()
        }

        setupThemeSwitch()
    }

    private fun setupThemeSwitch() {
        val switch = findViewById<SwitchCompat>(R.id.theme_switch) ?: return

        val isDarkMode = themeInteractor.isDarkModeEnabled()
        switch.isChecked = isDarkMode

        switch.setOnCheckedChangeListener { _, isChecked ->
            themeInteractor.setDarkMode(isChecked)
            applyTheme()
            recreate()
        }
    }

    private fun applyTheme() {
        val nightMode = if (themeInteractor.isDarkModeEnabled()) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(nightMode)
    }

    private fun sendEmail() {
        emailService.sendEmail(
            context = this,
            recipient = getString(R.string.rec_email),
            subject = getString(R.string.mail_title),
            body = getString(R.string.mail_text)
        )
    }
}
