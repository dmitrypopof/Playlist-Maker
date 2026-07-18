package com.example.playlistmaker

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.playlistmaker.helpers.TrackIntentHelper
import com.example.playlistmaker.models.Track
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView

class AudioPlayer : AppCompatActivity() {
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
            ?: run {
                // Если трек не передан, создаем заглушку
                Track(
                    trackName = "Unknown Track",
                    artistName = "Unknown Artist",
                    trackTime = 0,
                    artworkUrl100 = null,
                    collectionName = "Unknown Album",
                    releaseDate = "Unknown",
                    primaryGenreName = "Unknown",
                    country = "Unknown"
                )
            }

        // Инициализация UI элементов
        initViews()

        // Заполнение данными
        displayTrackInfo()

        // Настройка слушателей
        setupListeners()
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
        val gridLayout = findViewById<android.widget.GridLayout>(R.id.track_info)

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
        trackNameView.text = track.displayTrackName
        artistNameView.text = track.displayArtistName

        // Загружаем обложку альбома (используем artworkUrl100 для загрузки изображения)
        val imageView = albumCover.getChildAt(0) as androidx.appcompat.widget.AppCompatImageView
        Glide.with(this)
            .load(track.artworkUrl100?.replace("100x100", "512x512")) // Пытаемся загрузить изображение большего размера
            .placeholder(ContextCompat.getDrawable(this, R.drawable.ic_placeholder_no_download_45x45))
            .error(ContextCompat.getDrawable(this, R.drawable.ic_placeholder_no_download_45x45))
            .centerCrop()
            .into(imageView)

        // Заполняем информацию о треке
        durationView.text = track.formattedTime
        albumView.text = track.displayCollectionName
        yearView.text = track.displayYear
        genreView.text = track.displayGenre
        countryView.text = track.displayCountry

        // Отображаем время трека под кнопкой play
        trackTimeView.text = track.formattedTime
    }

    private fun setupListeners() {
        // Кнопка назад
        backButton.setOnClickListener {
            finish()
        }


    }

}