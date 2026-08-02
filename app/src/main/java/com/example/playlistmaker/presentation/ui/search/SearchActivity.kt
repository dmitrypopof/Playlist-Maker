package com.example.playlistmaker.presentation.ui.search

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.presentation.adapter.TrackAdapter
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView

class SearchActivity : AppCompatActivity() {

    private var inputValue: String = TEXT_DEF
    private lateinit var recyclerView: RecyclerView
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var placeholderSearch: LinearLayout
    private lateinit var stubNoResult: LinearLayout
    private lateinit var searchHistoryContainer: LinearLayout
    private lateinit var adapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var searchField: AppCompatEditText
    private lateinit var clearButton: ImageView
    private lateinit var hintMessage: MaterialTextView
    private lateinit var progressBar: ProgressBar

    // Use cases через Creator
    private val searchTracksUseCase by lazy { Creator.provideSearchTracksUseCase() }
    private val searchHistoryUseCase by lazy { Creator.provideSearchHistoryUseCase() }

    private val searchRunnable = Runnable {
        val currentQuery = searchField.text.toString()
        if (currentQuery.isNotEmpty()) {
            performSearch(currentQuery)
        }
    }
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val backButton = findViewById<MaterialButton>(R.id.back_button)

        recyclerView = findViewById(R.id.recyclerView)
        historyRecyclerView = findViewById(R.id.history_recycler_view)
        placeholderSearch = findViewById(R.id.placeholder_search)
        stubNoResult = findViewById(R.id.stub_no_result)
        searchHistoryContainer = findViewById(R.id.search_history_container)
        searchField = findViewById(R.id.search_field)
        clearButton = findViewById(R.id.clearIcon)
        hintMessage = findViewById(R.id.searchHint)
        progressBar = findViewById(R.id.progressBar)

        backButton.setOnClickListener {
            finish()
        }

        clearButton.setOnClickListener {
            searchField.setText("")
            inputValue = ""

            adapter.updateTracks(emptyList())

            hideAllContainers()
            showHistoryOrHint()

            searchField.clearFocus()
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(searchField.windowToken, 0)

            handler.removeCallbacks(searchRunnable)
        }

        searchField.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchField.text.toString().isEmpty()) {
                showHistoryOrHint()
            } else {
                hintMessage.visibility = View.GONE
                searchHistoryContainer.visibility = View.GONE
            }
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                inputValue = s?.toString() ?: ""
                clearButton.visibility = clearButtonVisibility(s)

                handler.removeCallbacks(searchRunnable)

                if (inputValue.isEmpty()) {
                    showHistoryOrHint()
                } else {
                    searchHistoryContainer.visibility = View.GONE
                    hintMessage.visibility = View.GONE
                    handler.postDelayed(searchRunnable, SEARCH_DELAY_MS)
                }
            }
        }

        searchField.addTextChangedListener(simpleTextWatcher)
        clearButton.visibility = clearButtonVisibility(searchField.text)

        // Адаптер для результатов поиска
        adapter = TrackAdapter(emptyList()) { track ->
            searchHistoryUseCase.addTrack(track)
            updateHistory()
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Адаптер для истории
        historyAdapter = TrackAdapter(emptyList()) { track ->
            searchHistoryUseCase.addTrack(track)
            updateHistory()
        }
        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        historyRecyclerView.adapter = historyAdapter

        // Кнопка очистки истории
        val clearHistoryButton = findViewById<MaterialButton>(R.id.clear_history_button)
        clearHistoryButton.setOnClickListener {
            searchHistoryUseCase.clearHistory()
            searchHistoryContainer.visibility = View.GONE

            if (searchField.text.toString().isEmpty()) {
                hintMessage.visibility = View.VISIBLE
            }
        }

        // Кнопка обновить
        val updateButton = findViewById<MaterialButton>(R.id.update_button)
        updateButton.setOnClickListener {
            if (inputValue.isNotEmpty()) {
                performSearch(inputValue)
            }
        }

        hideAllContainers()
        searchField.requestFocus()
    }

    private fun showHistoryOrHint() {
        val history = searchHistoryUseCase.getHistory()
        if (history.isNotEmpty()) {
            historyAdapter.updateTracks(history)
            searchHistoryContainer.visibility = View.VISIBLE
            hintMessage.visibility = View.GONE
        } else {
            hintMessage.visibility = View.VISIBLE
            searchHistoryContainer.visibility = View.GONE
        }
        recyclerView.visibility = View.GONE
        placeholderSearch.visibility = View.GONE
        stubNoResult.visibility = View.GONE
    }

    private fun hideAllContainers() {
        recyclerView.visibility = View.GONE
        placeholderSearch.visibility = View.GONE
        stubNoResult.visibility = View.GONE
        searchHistoryContainer.visibility = View.GONE
        hintMessage.visibility = View.GONE
        progressBar.visibility = View.GONE
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        placeholderSearch.visibility = View.GONE
        stubNoResult.visibility = View.GONE
        searchHistoryContainer.visibility = View.GONE
        hintMessage.visibility = View.GONE
    }

    private fun hideLoading() {
        progressBar.visibility = View.GONE
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    companion object {
        const val INPUT_TEXT = "INPUT_TEXT"
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
        recyclerView.visibility = View.VISIBLE
        placeholderSearch.visibility = View.GONE
        stubNoResult.visibility = View.GONE
        searchHistoryContainer.visibility = View.GONE

        adapter.updateTracks(tracks)
    }

    private fun showNoResult() {
        hideLoading()
        recyclerView.visibility = View.GONE
        placeholderSearch.visibility = View.GONE
        stubNoResult.visibility = View.VISIBLE
        searchHistoryContainer.visibility = View.GONE
    }

    private fun showNetworkError() {
        hideLoading()
        recyclerView.visibility = View.GONE
        placeholderSearch.visibility = View.VISIBLE
        stubNoResult.visibility = View.GONE
        searchHistoryContainer.visibility = View.GONE
    }

    private fun updateHistory() {
        val history = searchHistoryUseCase.getHistory()
        if (history.isNotEmpty()) {
            historyAdapter.updateTracks(history)
            if (searchField.text.toString().isEmpty() && searchField.hasFocus()) {
                searchHistoryContainer.visibility = View.VISIBLE
                hintMessage.visibility = View.GONE
            }
        }
    }
}