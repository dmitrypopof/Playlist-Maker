package com.example.playlistmaker.presentation.ui.player

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.GridLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.model.Track

import com.example.playlistmaker.presentation.helper.TrackIntentHelper
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayer : AppCompatActivity() {
    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }

    private val mainThreadHandler = Handler(Looper.getMainLooper())
    private lateinit var updateProgressRunnable: Runnable

    private lateinit var track: Track

    private lateinit var backButton: MaterialButton
    private lateinit var trackNameView: MaterialTextView
    private lateinit var artistNameView: MaterialTextView
    private lateinit var albumCover: CardView
    private lateinit var playButton: MaterialButton
    private lateinit var favouriteButton: MaterialButton
    private lateinit var addPlaylistButton: MaterialButton
    private lateinit var trackTimeView: MaterialTextView
    private lateinit var durationView: MaterialTextView
    private lateinit var albumView: MaterialTextView
    private lateinit var yearView: MaterialTextView
    private lateinit var genreView: MaterialTextView
    private lateinit var countryView: MaterialTextView

    private var mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_audioplayer)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.audioPlayer)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

// Получаем данные о треке
        track = TrackIntentHelper.getTrackFromIntent(intent)
            ?: Track.createDefault()

        initViews()
        displayTrackInfo()
        setupListeners()
        preparePlayer()

    }

    private fun initViews() {
        backButton = findViewById(R.id.back_button)
        trackNameView = findViewById(R.id.track_name)
        artistNameView = findViewById(R.id.artist_name)
        albumCover = findViewById(R.id.album_cover)
        playButton = findViewById(R.id.playButton)
        favouriteButton = findViewById(R.id.favouriteButton)
        addPlaylistButton = findViewById(R.id.addPlaylist)
        trackTimeView = findViewById(R.id.track_time)

        // Получаем элементы GridLayout через их ID (используем findViews по индексу или по ID)
        // Так как GridLayout не имеет прямых ID для TextView, используем findViewsByTag или getChildAt
        val gridLayout = findViewById<GridLayout>(R.id.track_info)

        // Используем getChildAt для получения TextView
        // Индексы: 0,1 - продолжительность; 2,3 - альбом; 4,5 - год; 6,7 - жанр; 8,9 - страна
        durationView = gridLayout.getChildAt(1) as MaterialTextView
        albumView = gridLayout.getChildAt(3) as MaterialTextView
        yearView = gridLayout.getChildAt(5) as MaterialTextView
        genreView = gridLayout.getChildAt(7) as MaterialTextView
        countryView = gridLayout.getChildAt(9) as MaterialTextView
    }

    private fun displayTrackInfo() {
        // Заполняем основные данные
        trackNameView.text = track.trackName
        artistNameView.text = track.artistName

        // Загружаем обложку альбома (используем artworkUrl100 для загрузки изображения)
        val imageView = albumCover.getChildAt(0) as AppCompatImageView
        Glide.with(this)
            .load(track.artworkUrl100.replace("100x100", "512x512")) // Пытаемся загрузить изображение большего размера
            .placeholder(ContextCompat.getDrawable(this, R.drawable.ic_placeholder_no_download_45x45))
            .error(ContextCompat.getDrawable(this, R.drawable.ic_placeholder_no_download_45x45))
            .centerCrop()
            .into(imageView)

        // Заполняем информацию о треке
        durationView.text = track.formattedTime
        albumView.text = track.collectionName
        yearView.text = track.displayYear
        genreView.text = track.primaryGenreName
        countryView.text = track.country

        // Отображаем время трека под кнопкой play
        trackTimeView.text = track.formattedTime
    }

    private fun setupListeners() {
        // Кнопка назад
        backButton.setOnClickListener {
            finish()
        }

        playButton.setOnClickListener {
            playbackControl()
        }
    }



    private fun preparePlayer() {
        val previewUrl = track.previewUrl
        if (previewUrl == null) {
            playButton.isEnabled = false
            return
        }

        mediaPlayer.apply {
            setDataSource(previewUrl)
            prepareAsync()
            setOnPreparedListener {
                playButton.isEnabled = true
                playerState = STATE_PREPARED
                trackTimeView.text = track.formattedTime
            }
            setOnCompletionListener {
                playerState = STATE_PREPARED
                playButton.setIconResource(R.drawable.ic_play_button)
                trackTimeView.text = getString(R.string.default_track_time)
                stopUpdateProgress()
            }
        }
    }


    private fun startPlayer() {
        mediaPlayer.apply {
            start()
            playerState = STATE_PLAYING
            playButton.setIconResource(R.drawable.ic_pause_button)
            startUpdateProgress()
        }
    }

    private fun pausePlayer() {
        mediaPlayer.apply {
            pause()
            playerState = STATE_PAUSED
            playButton.setIconResource(R.drawable.ic_play_button)
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
                    trackTimeView.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(currentPosition)
                    mainThreadHandler.postDelayed(this, 300)
                }
            }
        }
        mainThreadHandler.post(updateProgressRunnable)
    }

    private fun stopUpdateProgress() {
        mainThreadHandler.removeCallbacks(updateProgressRunnable)
    }
    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopUpdateProgress()
        mediaPlayer.release()
    }
}