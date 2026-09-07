package com.example.playlistmaker.feature.search.presentation

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.feature.search.domain.model.Track
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModel()

    private lateinit var adapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapters()
        setupListeners()
        observeState()
        observeEvents()

        hideAllContainers()
        binding.searchField.requestFocus()
    }

    override fun onResume() {
        super.onResume()
        if (binding.searchField.text.isNullOrEmpty()) {
            viewModel.refreshHistory()
        } else {
            viewModel.restoreSearchResults()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupAdapters() {
        adapter = TrackAdapter(emptyList()) { track ->
            viewModel.onTrackClicked(track)
        }

        historyAdapter = TrackAdapter(emptyList()) { track ->
            viewModel.onTrackClicked(track)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        binding.historyRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.historyRecyclerView.adapter = historyAdapter
    }

    private fun setupListeners() {

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

    private fun observeState() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            renderState(state)
        }
    }

    private fun observeEvents() {
        viewModel.events.observe(viewLifecycleOwner) { event ->
            when (event) {
                is SearchEvent.NavigateToPlayer -> {
                    viewModel.getTrackById(event.trackId)?.let { track ->
                        openAudioPlayer(track)
                    }
                }
            }
        }
    }

    private fun renderState(state: SearchState) {
        hideAllContainers()

        when (state) {
            is SearchState.Loading -> showLoading()
            is SearchState.Content -> showTracks(state.tracks)
            is SearchState.Empty -> showNoResult()
            is SearchState.HistoryContent -> showHistory(state.tracks)
            is SearchState.HistoryEmpty -> showHint()
            is SearchState.NetworkError -> showNetworkError()
        }
    }

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

    private fun openAudioPlayer(track: Track) {
        binding.searchField.clearFocus()
        hideKeyboard()

        // TODO: заменить на findNavController().navigate(...) с передачей аргументов
        // после подключения Jetpack Navigation Component
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchField.windowToken, 0)
        binding.searchField.clearFocus()
    }
}