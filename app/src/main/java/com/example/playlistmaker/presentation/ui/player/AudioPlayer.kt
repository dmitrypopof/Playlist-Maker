package com.example.playlistmaker.presentation.ui.player

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityAudioplayerBinding
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.presentation.helper.TrackIntentHelper
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayer : AppCompatActivity() {
    private lateinit var binding: ActivityAudioplayerBinding

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }

    private val mainThreadHandler = Handler(Looper.getMainLooper())
    private lateinit var updateProgressRunnable: Runnable

    private lateinit var track: Track

    private var mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT

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

        displayTrackInfo()
        setupListeners()
        preparePlayer()

    }

    private fun displayTrackInfo() {
        // Заполняем основные данные
        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName

        // Загружаем обложку альбома (используем artworkUrl100 для загрузки изображения)
        val imageView = binding.albumCover.getChildAt(0) as AppCompatImageView
        Glide.with(this)
            .load(
                track.artworkUrl100.replace(
                    "100x100",
                    "512x512"
                )
            ) // Пытаемся загрузить изображение большего размера
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

        binding.playButton.setOnClickListener {
            playbackControl()
        }
    }

    private fun preparePlayer() {
        val previewUrl = track.previewUrl
        if (previewUrl == null) {
            binding.playButton.isEnabled = false
            return
        }

        mediaPlayer.apply {
            setDataSource(previewUrl)
            prepareAsync()
            setOnPreparedListener {
                binding.playButton.isEnabled = true
                playerState = STATE_PREPARED
                binding.trackTime.text = track.formattedTime
            }
            setOnCompletionListener {
                playerState = STATE_PREPARED
                binding.playButton.setIconResource(R.drawable.ic_play_button)
                binding.trackTime.text = getString(R.string.default_track_time)
                stopUpdateProgress()
            }
        }
    }


    private fun startPlayer() {
        mediaPlayer.apply {
            start()
            playerState = STATE_PLAYING
            binding.playButton.setIconResource(R.drawable.ic_pause_button)
            startUpdateProgress()
        }
    }

    private fun pausePlayer() {
        // Останавливаем плеер, только если он реально играет
        if (playerState == STATE_PLAYING) {
            mediaPlayer.pause()
            playerState = STATE_PAUSED
            binding.playButton.setIconResource(R.drawable.ic_play_button)
            stopUpdateProgress()
        }
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> pausePlayer()
            STATE_PREPARED, STATE_PAUSED -> startPlayer()
        }
    }

    private fun startUpdateProgress() {
        updateProgressRunnable = object : Runnable {
            override fun run() {
                if (playerState == STATE_PLAYING) {
                    val currentPosition = mediaPlayer.currentPosition
                    binding.trackTime.text =
                        SimpleDateFormat("mm:ss", Locale.getDefault()).format(currentPosition)
                    mainThreadHandler.postDelayed(this, 300)
                }
            }
        }
        mainThreadHandler.post(updateProgressRunnable)
    }

    private fun stopUpdateProgress() {
        // Проверяем, был ли инициализирован Runnable
        if (::updateProgressRunnable.isInitialized) {
            mainThreadHandler.removeCallbacks(updateProgressRunnable)
        }
    }

    override fun onPause() {
        super.onPause()
        // Не вызываем pausePlayer() здесь, если плеер не в состоянии PLAYING,
        // или оставляем как есть, но с защищённым pausePlayer() – он сам проверит.
        if (playerState == STATE_PLAYING) {
            pausePlayer()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopUpdateProgress()
        mediaPlayer.release()
    }
}