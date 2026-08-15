package com.example.playlistmaker.feature.player.presentation

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityAudioplayerBinding
import com.example.playlistmaker.feature.search.domain.model.Track
import com.example.playlistmaker.core.utils.TrackIntentHelper

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAudioplayerBinding
    private lateinit var viewModel: AudioPlayerViewModel

    private lateinit var track: Track

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAudioplayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.audioPlayer) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Получаем данные о треке
        track = TrackIntentHelper.getTrackFromIntent(intent)
            ?: Track.createDefault()

        // Инициализация ViewModel
        viewModel = ViewModelProvider(
            this,
            AudioPlayerViewModelFactory()
        )[AudioPlayerViewModel::class.java]

        // Отображаем информацию о треке
        displayTrackInfo()

        // Настройка слушателей
        setupListeners()

        // Наблюдение за состоянием
        observeState()

        // Загружаем трек в плеер
        viewModel.loadTrack(track)
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        // ViewModel сама освободит ресурсы в onCleared()
    }

    private fun displayTrackInfo() {
        // Заполняем основные данные
        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName

        // Загружаем обложку альбома
        val imageView = binding.albumCover.getChildAt(0) as AppCompatImageView
        Glide.with(this)
            .load(
                track.artworkUrl100.replace(
                    "100x100",
                    "512x512"
                )
            )
            .placeholder(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_placeholder_no_download_45x45
                )
            )
            .error(ContextCompat.getDrawable(this, R.drawable.ic_placeholder_no_download_45x45))
            .centerCrop()
            .into(imageView)

        // Заполняем информацию о треке
        binding.apply {
            durationValue.text = track.formattedTime
            albumValue.text = track.collectionName
            yearValue.text = track.displayYear
            genreValue.text = track.primaryGenreName
            countryValue.text = track.country
        }

        // Отображаем время трека под кнопкой play
        binding.trackTime.text = track.formattedTime
    }

    private fun setupListeners() {
        // Кнопка назад
        binding.backButton.setOnClickListener {
            finish()
        }

        // Кнопка play/pause
        binding.playButton.setOnClickListener {
            viewModel.playbackControl()
        }
    }

    private fun observeState() {
        viewModel.state.observe(this) { state ->
            renderState(state)
        }
    }

    private fun renderState(state: AudioPlayerState) {
        when (state) {
            is AudioPlayerState.Content -> {
                // Информация о треке уже отображена
            }
            is AudioPlayerState.Prepared -> {
                binding.playButton.setIconResource(R.drawable.ic_play_button)
                binding.trackTime.text = track.formattedTime
            }
            is AudioPlayerState.Playing -> {
                binding.playButton.setIconResource(R.drawable.ic_pause_button)
            }
            is AudioPlayerState.Paused -> {
                binding.playButton.setIconResource(R.drawable.ic_play_button)
            }
            is AudioPlayerState.Progress -> {
                binding.trackTime.text = state.currentPosition
            }
            is AudioPlayerState.Default -> {
                // Начальное состояние
            }
        }
    }
}