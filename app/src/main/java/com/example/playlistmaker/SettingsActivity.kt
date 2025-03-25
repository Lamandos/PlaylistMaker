
package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingsActivity : AppCompatActivity() {
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

        val backButton = findViewById<ImageButton>(R.id.settings_btn_back)
        backButton.setOnClickListener {
            finish()
        }

        val agreementButton: ImageView = findViewById(R.id.agreement)
        agreementButton.setOnClickListener {
            openUserAgreement()
        }

        val agreementText: TextView = findViewById(R.id.agreementText)
        agreementText.setOnClickListener {
            openUserAgreement()
        }

        val shareButton: ImageView = findViewById(R.id.share)
        shareButton.setOnClickListener {
            shareApp()
        }

        val shareText: TextView = findViewById(R.id.shareText)
        shareText.setOnClickListener {
            shareApp()
        }

        val writeToSupportButton: ImageView = findViewById(R.id.support)
        writeToSupportButton.setOnClickListener {
            sendEmailToSupport()
        }

        val writeToSupportText: TextView = findViewById(R.id.supportText)
        writeToSupportText.setOnClickListener {
            sendEmailToSupport()
        }
    }

    private fun openUserAgreement() {
        val url = getString(R.string.agreement_text)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    private fun shareApp() {

        val message = getString(R.string.share_text)

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, message)
        }

        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_opt)))
    }

    private fun sendEmailToSupport() {
        val recipientEmail = getString(R.string.rec_email)
        val subject = getString(R.string.mail_title)
        val body = getString(R.string.mail_text)

        val emailUri = Uri.parse("mailto:")
            .buildUpon()
            .appendQueryParameter("to", recipientEmail)
            .appendQueryParameter("subject", subject)
            .appendQueryParameter("body", body)
            .build()

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = emailUri
        }
        startActivity(intent)

    }
}