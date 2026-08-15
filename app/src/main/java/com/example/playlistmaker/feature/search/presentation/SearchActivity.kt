package com.example.playlistmaker.feature.search.presentation

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.Creator
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.feature.search.domain.model.Track
import com.example.playlistmaker.presentation.helper.TrackIntentHelper
import com.example.playlistmaker.presentation.ui.player.AudioPlayer

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var viewModel: SearchViewModel  // <-- ДОБАВИТЬ ViewModel

    private lateinit var adapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // ===== ИНИЦИАЛИЗАЦИЯ VIEWMODEL =====
        viewModel = ViewModelProvider(
            this,
            SearchViewModelFactory(
                Creator.provideSearchTracksUseCase(),
                Creator.provideGetSearchHistoryUseCase(),
                Creator.provideAddTrackToHistoryUseCase(),
                Creator.provideClearSearchHistoryUseCase()
            )
        )[SearchViewModel::class.java]

        // ===== НАСТРОЙКА АДАПТЕРОВ =====
        setupAdapters()

        // ===== НАСТРОЙКА СЛУШАТЕЛЕЙ =====
        setupListeners()

        // ===== НАБЛЮДЕНИЕ ЗА СОСТОЯНИЕМ =====
        observeState()

        hideAllContainers()
        binding.searchField.requestFocus()
    }

    override fun onResume() {
        super.onResume()
        // Если поле поиска пустое, обновляем историю
        if (binding.searchField.text.isNullOrEmpty()) {
            // Обновляем историю из репозитория
            viewModel.refreshHistory()
        }
    }

    // ===== НОВЫЙ МЕТОД ДЛЯ НАСТРОЙКИ АДАПТЕРОВ =====
    private fun setupAdapters() {
        adapter = TrackAdapter(emptyList()) { track ->
            viewModel.onTrackClicked(track)
            //openAudioPlayer(track)
        }

        historyAdapter = TrackAdapter(emptyList()) { track ->
            viewModel.onTrackClicked(track)
            //openAudioPlayer(track)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        binding.historyRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.historyRecyclerView.adapter = historyAdapter
    }

    // ===== НОВЫЙ МЕТОД ДЛЯ НАСТРОЙКИ СЛУШАТЕЛЕЙ =====
    private fun setupListeners() {
        binding.backButton.setOnClickListener {
            finish()
        }

        binding.clearIcon.setOnClickListener {
            viewModel.onClearQueryClicked()
            binding.searchField.setText("")
            binding.searchField.clearFocus()
            hideKeyboard()
        }

        binding.searchField.setOnFocusChangeListener { _, hasFocus ->
            viewModel.onSearchFocusChanged(hasFocus)
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s?.toString() ?: ""
                binding.clearIcon.visibility = if (query.isNotEmpty()) View.VISIBLE else View.GONE
                viewModel.onSearchTextChanged(query)
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        binding.searchField.addTextChangedListener(textWatcher)

        binding.clearHistoryButton.setOnClickListener {
            viewModel.onClearHistoryClicked()
        }

        binding.updateButton.setOnClickListener {
            viewModel.onRetryClicked()
        }

        binding.main.setOnClickListener {
            hideKeyboard()
        }
    }

    // ===== НАБЛЮДЕНИЕ ЗА СОСТОЯНИЕМ =====
    private fun observeState() {
        viewModel.state.observe(this) { state ->
            renderState(state)

            // Обрабатываем навигацию отдельно
            if (state is SearchState.NavigateToPlayer) {
                openAudioPlayer()
            }
        }
    }

    // ===== ОТРИСОВКА СОСТОЯНИЙ =====
    private fun renderState(state: SearchState) {
        hideAllContainers()

        when (state) {
            is SearchState.Loading -> showLoading()
            is SearchState.Content -> showTracks(state.tracks)
            is SearchState.Empty -> showNoResult()
            is SearchState.HistoryContent -> showHistory(state.tracks)
            is SearchState.HistoryEmpty -> showHint()
            is SearchState.NetworkError -> showNetworkError()
            is SearchState.NavigateToPlayer -> {
                // Ничего не показываем, просто открываем плеер
                // Состояние не меняем, чтобы не было мерцания
            }
        }
    }

    // ===== МЕТОДЫ ОТОБРАЖЕНИЯ =====
    private fun showHistory(tracks: List<Track>) {
        historyAdapter.updateTracks(tracks)
        binding.searchHistoryContainer.visibility = View.VISIBLE
    }

    private fun showHint() {
        binding.searchHint.visibility = View.VISIBLE
    }

    private fun showTracks(tracks: List<Track>) {
        adapter.updateTracks(tracks)
        binding.recyclerView.visibility = View.VISIBLE
    }

    private fun showNoResult() {
        binding.stubNoResult.visibility = View.VISIBLE
    }

    private fun showNetworkError() {
        binding.placeholderSearch.visibility = View.VISIBLE
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideAllContainers() {
        binding.apply {
            recyclerView.visibility = View.GONE
            placeholderSearch.visibility = View.GONE
            stubNoResult.visibility = View.GONE
            searchHistoryContainer.visibility = View.GONE
            searchHint.visibility = View.GONE
            progressBar.visibility = View.GONE
        }
    }

    // ===== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ =====
    private fun openAudioPlayer() {
        val track = viewModel.consumeSelectedTrack()
        if (track != null) {
            binding.searchField.text?.clear()
            binding.searchField.clearFocus()
            hideKeyboard()

            val intent = Intent(this, AudioPlayer::class.java)
            TrackIntentHelper.putTrackToIntent(intent, track)
            startActivity(intent)
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchField.windowToken, 0)
        binding.searchField.clearFocus()
    }
}