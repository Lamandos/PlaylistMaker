package com.example.playlistmaker.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.interactor.ThemeInteractor

class SettingsActivity : AppCompatActivity() {
    private val emailService = Creator.provideEmailService()
    private val shareService = Creator.provideShareService()
    private val userAgreementService = Creator.provideUserAgreementService()
    private val themeInteractor: ThemeInteractor = Creator.themeInteractor

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<ImageButton>(R.id.settings_btn_back).setOnClickListener { finish() }

        findViewById<ImageView>(R.id.agreement).setOnClickListener {
            userAgreementService.openUserAgreement(this, getString(R.string.agreement_text))
        }
        findViewById<TextView>(R.id.agreementText).setOnClickListener {
            userAgreementService.openUserAgreement(this, getString(R.string.agreement_text))
        }

        findViewById<ImageView>(R.id.share).setOnClickListener {
            shareService.shareApp(this, getString(R.string.share_text))
        }
        findViewById<TextView>(R.id.shareText).setOnClickListener {
            shareService.shareApp(this, getString(R.string.share_text))
        }

        findViewById<ImageView>(R.id.support).setOnClickListener {
            sendEmail()
        }
        findViewById<TextView>(R.id.supportText).setOnClickListener {
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
            themeInteractor.applyTheme()
            recreate()
        }
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
