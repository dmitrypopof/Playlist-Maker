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
    private lateinit var placeholderSearch: LinearLayout
    private lateinit var stubNoResult: LinearLayout
    private lateinit var adapter: TrackAdapter
    private lateinit var searchField: AppCompatEditText
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

        val backButton = findViewById<MaterialButton>(R.id.back_button)

        recyclerView = findViewById(R.id.recyclerView)
        placeholderSearch = findViewById(R.id.placeholder_search)
        stubNoResult = findViewById(R.id.stub_no_result)
        searchField = findViewById(R.id.search_field)
        hintMessage = findViewById(R.id.searchHint)

        val clearButton = findViewById<ImageView>(R.id.clearIcon)

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
            recyclerView.visibility = View.GONE
            placeholderSearch.visibility = View.GONE
            stubNoResult.visibility = View.GONE

            searchField.clearFocus()
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(searchField.windowToken, 0)

            // Отменяем отложенный поиск
            handler.removeCallbacks(searchRunnable)
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

                // Отменяем предыдущий запрос на поиск
                handler.removeCallbacks(searchRunnable)

                // Если текст не пустой - запускаем отложенный поиск
                if (inputValue.isNotEmpty()) {
                    handler.postDelayed(searchRunnable, SEARCH_DELAY_MS)
                }
            }

        }

        //логика отображения хинта в поле поиска:
        searchField.setOnFocusChangeListener {view, hasFocus ->
            hintMessage.visibility =
                if (hasFocus && searchField.text.toString().isEmpty())
                View.VISIBLE else View.GONE
        }
        searchField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                hintMessage.visibility = if (searchField.hasFocus() && p0?.isEmpty() == true) View.VISIBLE else View.GONE
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        searchField.addTextChangedListener(simpleTextWatcher)
        clearButton.visibility = clearButtonVisibility(searchField.text)
        //searchField.requestFocus()

        adapter = TrackAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // При старте скрываем все (пустой экран)
        recyclerView.visibility = View.GONE
        placeholderSearch.visibility = View.GONE
        stubNoResult.visibility = View.GONE

        // обработка кнопки обновить
        val updateButton = findViewById<MaterialButton>(R.id.update_button)
        updateButton.setOnClickListener {
            if (inputValue.isNotEmpty()) {
                performSearch(inputValue)
            }
        }
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

    companion object{
        const val INPUT_TEXT = "INPUT_TEXT"
        const val TEXT_DEF = ""
        const val SEARCH_DELAY_MS = 2000L
    }

    private fun performSearch(query: String) {
        android.util.Log.d("SearchActivity", "Поисковой запрос: $query")
        // Показываем RecyclerView, скрываем заглушки
        recyclerView.visibility = View.VISIBLE
        placeholderSearch.visibility = View.GONE
        stubNoResult.visibility = View.GONE

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
                        // Ничего не нашлось
                        showNoResult()
                    } else {
                        // Есть результаты
                        showTracks(tracks)
                    }
                } else {
                    // Ошибка сервера (например, 500)
                    showNetworkError()
                }
            }

            override fun onFailure(
                call: retrofit2.Call<TrackResponse>,
                t: Throwable
            ) {
                // Ошибка сети
                showNetworkError()
            }
        })
    }

    private fun showTracks(tracks: List<Track>) {
        recyclerView.visibility = View.VISIBLE
        placeholderSearch.visibility = View.GONE
        stubNoResult.visibility = View.GONE

        adapter = TrackAdapter(tracks)
        recyclerView.adapter = adapter
    }

    private fun showNoResult() {
        recyclerView.visibility = View.GONE
        placeholderSearch.visibility = View.GONE
        stubNoResult.visibility = View.VISIBLE
    }

    private fun showNetworkError() {
        recyclerView.visibility = View.GONE
        placeholderSearch.visibility = View.VISIBLE
        stubNoResult.visibility = View.GONE
    }
}