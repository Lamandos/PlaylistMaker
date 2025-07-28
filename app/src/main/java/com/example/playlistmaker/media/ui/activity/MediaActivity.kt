package com.example.playlistmaker.media.ui.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityMediaBinding
import com.example.playlistmaker.media.ui.fragments.MediaPagerAdapter
import com.example.playlistmaker.media.ui.view_model.MediaViewModel
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaActivity : AppCompatActivity() {

    private val viewModel: MediaViewModel by viewModel()
    private lateinit var binding: ActivityMediaBinding
    private lateinit var tabMediator: TabLayoutMediator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMediaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.mediaBtnBack.setOnClickListener { finish() }
        binding.viewPager.adapter = MediaPagerAdapter(this)

        tabMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when(position) {
                0 -> getString(R.string.favorites)
                1 -> getString(R.string.playlists)
                else -> ""
            }
        }
        tabMediator.attach()
    }

    override fun onDestroy() {
        super.onDestroy()
        tabMediator.detach()
    }
}