package com.example.playlistmaker.feature.main.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.playlistmaker.databinding.ActivityMainBinding
import com.example.playlistmaker.feature.search.presentation.SearchActivity
import com.example.playlistmaker.feature.settings.presentation.SettingsActivity
import com.example.playlistmaker.feature.media.presentation.MediaActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(top = systemBars.top, bottom = systemBars.bottom)
            insets
        }

        // Настройка слушателей
        setupListeners()
    }

    private fun setupListeners() {
        // Кнопка поиска
        binding.searchButton.setOnClickListener {
            navigateToSearch()
        }

        // Кнопка медиатеки
        binding.mediaButton.setOnClickListener {
            navigateToMedia()
        }

        // Кнопка настроек
        binding.settingButton.setOnClickListener {
            navigateToSettings()
        }
    }

    private fun navigateToSearch() {
        val intent = Intent(this, SearchActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToMedia() {
        val intent = Intent(this, MediaActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToSettings() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }
}