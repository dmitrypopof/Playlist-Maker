package com.example.playlistmaker.feature.media.presentation.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentFavoriteTracksBinding
import com.example.playlistmaker.feature.player.presentation.AudioPlayerFragment
import com.example.playlistmaker.feature.search.domain.model.Track
import com.example.playlistmaker.feature.search.presentation.TrackAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTracksFragment : Fragment() {

    private var _binding: FragmentFavoriteTracksBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavoriteTracksViewModel by viewModel()

    private var adapter: TrackAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()
        observeState()
        observeEvents()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null // ADDED
        binding.favoriteTracksRecyclerView.adapter = null
        _binding = null
    }

    private fun setupAdapter() {
        adapter = TrackAdapter(emptyList(), viewLifecycleOwner) { track ->
            viewModel.onTrackClicked(track)
        }
        binding.favoriteTracksRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.favoriteTracksRecyclerView.adapter = adapter
    }

    private fun observeState() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            renderState(state)
        }
    }

    private fun observeEvents() {
        viewModel.events.observe(viewLifecycleOwner) { event ->
            when (event) {
                is FavoriteTracksEvent.NavigateToPlayer -> {
                    openAudioPlayer(event.track)
                }
            }
        }
    }

    private fun renderState(state: FavoriteTracksState) {
        when (state) {
            is FavoriteTracksState.Content -> showContent(state.tracks)
            is FavoriteTracksState.Empty -> showEmpty()
        }
    }

    private fun showContent(tracks: List<Track>) {
        binding.favoriteTracksRecyclerView.visibility = View.VISIBLE
        binding.placeholderImage.visibility = View.GONE
        binding.placeholderText.visibility = View.GONE
        adapter?.updateTracks(tracks)
    }

    private fun showEmpty() {
        binding.favoriteTracksRecyclerView.visibility = View.GONE
        binding.placeholderImage.visibility = View.VISIBLE
        binding.placeholderText.visibility = View.VISIBLE
    }

    private fun openAudioPlayer(track: Track) {
        findNavController().navigate(
            R.id.action_mediaFragment_to_audioPlayerFragment,
            AudioPlayerFragment.createArgs(track)
        )
    }

    companion object {
        fun newInstance(): FavoriteTracksFragment = FavoriteTracksFragment()
    }
}