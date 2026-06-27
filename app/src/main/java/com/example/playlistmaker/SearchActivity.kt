package com.example.playlistmaker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.adapters.TrackAdapter
import com.example.playlistmaker.helpers.RetrofitHelper
import com.example.playlistmaker.models.Track
import com.example.playlistmaker.models.TrackResponse
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
    private lateinit var searchHistory: SearchHistory
    private lateinit var hintMessage: MaterialTextView

    private val searchRunnable = Runnable {
        performSearch(inputValue)
    }
    private val handler = android.os.Handler(android.os.Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Инициализация SearchHistory
        searchHistory = SearchHistory(this)

        val backButton = findViewById<MaterialButton>(R.id.back_button)

        recyclerView = findViewById(R.id.recyclerView)
        historyRecyclerView = findViewById(R.id.history_recycler_view)
        placeholderSearch = findViewById(R.id.placeholder_search)
        stubNoResult = findViewById(R.id.stub_no_result)
        searchHistoryContainer = findViewById(R.id.search_history_container)
        searchField = findViewById(R.id.search_field)
        clearButton = findViewById(R.id.clearIcon)
        hintMessage = findViewById(R.id.searchHint)

        backButton.setOnClickListener {
            finish()
        }

        clearButton.setOnClickListener {
            searchField.setText("")
            inputValue = ""

            // Очищаем список треков
            adapter = TrackAdapter(emptyList())
            recyclerView.adapter = adapter

            // Скрываем RecyclerView и заглушки
            hideAllContainers()

            // Показываем историю, если поле в фокусе
            showHistoryOrHint()

            searchField.clearFocus()
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(searchField.windowToken, 0)

            // Отменяем отложенный поиск
            handler.removeCallbacks(searchRunnable)
        }

        // Отслеживание фокуса для показа истории
        searchField.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchField.text.toString().isEmpty()) {
                showHistoryOrHint()
            } else {
                hintMessage.visibility = View.GONE
                searchHistoryContainer.visibility = View.GONE
            }
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // empty
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                inputValue = s?.toString() ?: ""
                clearButton.visibility = clearButtonVisibility(s)

                // Если текст изменился и стал пустым - показываем историю
                if (inputValue.isEmpty()) {
                    showHistoryOrHint()
                    handler.removeCallbacks(searchRunnable)
                } else {
                    // Скрываем историю и хинт при вводе текста
                    searchHistoryContainer.visibility = View.GONE
                    hintMessage.visibility = View.GONE
                    // Отменяем предыдущий запрос на поиск
                    handler.removeCallbacks(searchRunnable)
                    // Запускаем отложенный поиск
                    handler.postDelayed(searchRunnable, SEARCH_DELAY_MS)
                }
            }
        }

        searchField.addTextChangedListener(simpleTextWatcher)
        clearButton.visibility = clearButtonVisibility(searchField.text)

        // Настройка RecyclerView для результатов поиска
        adapter = TrackAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Настройка RecyclerView для истории
        historyAdapter = TrackAdapter(emptyList())
        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        historyRecyclerView.adapter = historyAdapter

        // Кнопка очистки истории
        val clearHistoryButton = findViewById<MaterialButton>(R.id.clear_history_button)
        clearHistoryButton.setOnClickListener {
            searchHistory.clearHistory()
            searchHistoryContainer.visibility = View.GONE
        }

        // Кнопка обновить (при ошибке сети)
        val updateButton = findViewById<MaterialButton>(R.id.update_button)
        updateButton.setOnClickListener {
            if (inputValue.isNotEmpty()) {
                performSearch(inputValue)
            }
        }

        // При старте скрываем все
        hideAllContainers()

        // Запрашиваем фокус на поле ввода
        searchField.requestFocus()
    }

    private fun showHistoryOrHint() {
        val history = searchHistory.getHistory()
        if (history.isNotEmpty()) {
            // Показываем историю
            historyAdapter = TrackAdapter(history) { track ->
                searchHistory.addTrack(track)
            }
            historyRecyclerView.adapter = historyAdapter
            searchHistoryContainer.visibility = View.VISIBLE
            hintMessage.visibility = View.GONE
        } else {
            // Показываем хинт
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
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(INPUT_TEXT, inputValue)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        inputValue = savedInstanceState.getString(INPUT_TEXT, TEXT_DEF)
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
        // Показываем RecyclerView, скрываем заглушки и историю
        recyclerView.visibility = View.VISIBLE
        placeholderSearch.visibility = View.GONE
        stubNoResult.visibility = View.GONE
        searchHistoryContainer.visibility = View.GONE

        // Выполняем запрос
        val call = RetrofitHelper.apiService.search(query)
        call.enqueue(object : retrofit2.Callback<TrackResponse> {
            override fun onResponse(
                call: retrofit2.Call<TrackResponse>,
                response: retrofit2.Response<TrackResponse>
            ) {
                if (response.isSuccessful) {
                    val trackResponse = response.body()
                    val tracks = trackResponse?.results ?: emptyList()

                    if (tracks.isEmpty()) {
                        showNoResult()
                    } else {
                        showTracks(tracks)
                    }
                } else {
                    showNetworkError()
                }
            }

            override fun onFailure(
                call: retrofit2.Call<TrackResponse>,
                t: Throwable
            ) {
                showNetworkError()
            }
        })
    }

    private fun showTracks(tracks: List<Track>) {
        recyclerView.visibility = View.VISIBLE
        placeholderSearch.visibility = View.GONE
        stubNoResult.visibility = View.GONE
        searchHistoryContainer.visibility = View.GONE

        adapter = TrackAdapter(tracks) { track ->
            searchHistory.addTrack(track)  // Сохраняем трек в историю при клике
        }

        recyclerView.adapter = adapter
    }

    private fun showNoResult() {
        recyclerView.visibility = View.GONE
        placeholderSearch.visibility = View.GONE
        stubNoResult.visibility = View.VISIBLE
        searchHistoryContainer.visibility = View.GONE
    }

    private fun showNetworkError() {
        recyclerView.visibility = View.GONE
        placeholderSearch.visibility = View.VISIBLE
        stubNoResult.visibility = View.GONE
        searchHistoryContainer.visibility = View.GONE
    }
}