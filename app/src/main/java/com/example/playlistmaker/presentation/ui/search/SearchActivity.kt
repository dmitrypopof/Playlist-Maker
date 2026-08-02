package com.example.playlistmaker.presentation.ui.search

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.Creator
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.presentation.adapter.TrackAdapter
import com.example.playlistmaker.presentation.helper.TrackIntentHelper
import com.example.playlistmaker.presentation.ui.player.AudioPlayer

class SearchActivity : AppCompatActivity() {

    private val tag = "SearchActivityLifecycle"
    private lateinit var binding: ActivitySearchBinding

    private var inputValue: String = TEXT_DEF
    private lateinit var adapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    // Use cases через Creator
    private val searchTracksUseCase by lazy { Creator.provideSearchTracksUseCase() }
    private val searchHistoryUseCase by lazy { Creator.provideSearchHistoryUseCase() }

    private val searchRunnable = Runnable {
        val currentQuery = binding.searchField.text.toString()
        if (currentQuery.isNotEmpty()) {
            performSearch(currentQuery)
        }
    }
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(tag, "onCreate")
        enableEdgeToEdge()
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.main.setOnClickListener {
            hideKeyboard()
        }

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.clearIcon.setOnClickListener {
            binding.searchField.setText("")
            inputValue = ""

            adapter.updateTracks(emptyList())

            hideAllContainers()
            showHistoryOrHint()

            binding.searchField.clearFocus()
            hideKeyboard()

            handler.removeCallbacks(searchRunnable)
        }

        binding.searchField.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.searchField.text.toString().isEmpty()) {
                showHistoryOrHint()
            } else {
                binding.searchHint.visibility = View.GONE
                binding.searchHistoryContainer.visibility = View.GONE
            }
        }
        // TextWatcher для поиска
        val simpleTextWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                inputValue = s?.toString() ?: ""
                binding.clearIcon.visibility = clearButtonVisibility(s)

                handler.removeCallbacks(searchRunnable)

                if (inputValue.isEmpty()) {
                    showHistoryOrHint()
                } else {
                    binding.searchHistoryContainer.visibility = View.GONE
                    binding.searchHint.visibility = View.GONE
                    handler.postDelayed(searchRunnable, SEARCH_DELAY_MS)
                }
            }
        }

        binding.searchField.addTextChangedListener(simpleTextWatcher)
        binding.clearIcon.visibility = clearButtonVisibility(binding.searchField.text)

        // Адаптер для результатов поиска
        adapter = TrackAdapter(emptyList()) { track ->
            searchHistoryUseCase.addTrack(track)
            updateHistory()
            openAudioPlayer(track)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        // Адаптер для истории
        historyAdapter = TrackAdapter(emptyList()) { track ->
            searchHistoryUseCase.addTrack(track)
            updateHistory()
            openAudioPlayer(track)
        }
        binding.historyRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.historyRecyclerView.adapter = historyAdapter

        // Кнопка очистки истории
        binding.clearHistoryButton.setOnClickListener {
            searchHistoryUseCase.clearHistory()
            binding.searchHistoryContainer.visibility = View.GONE

            if (binding.searchField.text.toString().isEmpty()) {
                binding.searchHint.visibility = View.VISIBLE
            }
        }

        // Кнопка обновить
        binding.updateButton.setOnClickListener {
            if (inputValue.isNotEmpty()) {
                performSearch(inputValue)
            }
        }

        hideAllContainers()
        binding.searchField.requestFocus()
    }

    override fun onResume() {
        super.onResume()
        // Если поле поиска пустое, показываем историю (или подсказку)
        if (binding.searchField.text.isNullOrEmpty()) {
            showHistoryOrHint()
        }
    }

    // ---------- Вспомогательные методы ----------

    private fun showHistoryOrHint() {
        val history = searchHistoryUseCase.getHistory()
        if (history.isNotEmpty()) {
            historyAdapter.updateTracks(history)
            binding.searchHistoryContainer.visibility = View.VISIBLE
            binding.searchHint.visibility = View.GONE
        } else {
            binding.searchHint.visibility = View.VISIBLE
            binding.searchHistoryContainer.visibility = View.GONE
        }
        binding.recyclerView.visibility = View.GONE
        binding.placeholderSearch.visibility = View.GONE
        binding.stubNoResult.visibility = View.GONE
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

    private fun showLoading() {
        binding.apply {
            progressBar.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            placeholderSearch.visibility = View.GONE
            stubNoResult.visibility = View.GONE
            searchHistoryContainer.visibility = View.GONE
            searchHint.visibility = View.GONE
        }
    }

    private fun hideLoading() {
        binding.progressBar.visibility = View.GONE
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    companion object {
        const val TEXT_DEF = ""
        const val SEARCH_DELAY_MS = 2000L
    }

    private fun performSearch(query: String) {
        showLoading()

        Thread {
            val result = searchTracksUseCase(query)

            runOnUiThread {
                hideLoading()

                result.onSuccess { tracks ->
                    if (tracks.isEmpty()) {
                        showNoResult()
                    } else {
                        showTracks(tracks)
                    }
                }.onFailure {
                    showNetworkError()
                }
            }
        }.start()
    }

    private fun showTracks(tracks: List<Track>) {
        hideLoading()
        binding.apply {
            recyclerView.visibility = View.VISIBLE
            placeholderSearch.visibility = View.GONE
            stubNoResult.visibility = View.GONE
            searchHistoryContainer.visibility = View.GONE
        }
        adapter.updateTracks(tracks)
    }

    private fun showNoResult() {
        hideLoading()
        binding.apply {
            recyclerView.visibility = View.GONE
            placeholderSearch.visibility = View.GONE
            stubNoResult.visibility = View.VISIBLE
            searchHistoryContainer.visibility = View.GONE
        }
    }

    private fun showNetworkError() {
        hideLoading()
        binding.apply {
            recyclerView.visibility = View.GONE
            placeholderSearch.visibility = View.VISIBLE
            stubNoResult.visibility = View.GONE
            searchHistoryContainer.visibility = View.GONE
        }
    }

    private fun updateHistory() {
        val history = searchHistoryUseCase.getHistory()
        if (history.isNotEmpty()) {
            historyAdapter.updateTracks(history)
            if (binding.searchField.text.toString().isEmpty() && binding.searchField.hasFocus()) {
                binding.searchHistoryContainer.visibility = View.VISIBLE
                binding.searchHint.visibility = View.GONE
            }
        }
    }

    private fun openAudioPlayer(track: Track) {
        binding.searchField.text?.clear()
        binding.searchField.clearFocus()
        hideKeyboard()

        val intent = Intent(this, AudioPlayer::class.java)
        TrackIntentHelper.putTrackToIntent(intent, track)
        startActivity(intent)
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchField.windowToken, 0)
        binding.searchField.clearFocus()
    }
}