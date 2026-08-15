package com.example.playlistmaker.feature.main.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.databinding.ActivityMainBinding
import com.example.playlistmaker.feature.search.presentation.SearchActivity
import com.example.playlistmaker.feature.settings.presentation.SettingsActivity
import com.example.playlistmaker.feature.media.presentation.MediaActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

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

        // Инициализация ViewModel
        viewModel = ViewModelProvider(
            this,
            MainViewModelFactory()
        )[MainViewModel::class.java]

        // Настройка слушателей
        setupListeners()
    }

    private fun setupListeners() {
        // Кнопка поиска
        binding.searchButton.setOnClickListener {
            val searchIntent = Intent(this@MainActivity, SearchActivity::class.java)
            startActivity(searchIntent)
        }

        // Кнопка медиатеки
        binding.mediaButton.setOnClickListener {
            val mediaIntent = Intent(this@MainActivity, MediaActivity::class.java)
            startActivity(mediaIntent)
        }

        // Кнопка настроек
        binding.settingButton.setOnClickListener {
            val setIntent = Intent(this@MainActivity, SettingsActivity::class.java)
            startActivity(setIntent)
        }
    }
}