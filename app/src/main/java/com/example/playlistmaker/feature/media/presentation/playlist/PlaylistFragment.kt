package com.example.playlistmaker.feature.media.presentation.playlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.feature.media.domain.model.Playlist
import com.example.playlistmaker.feature.search.domain.model.Track
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlaylistFragment : Fragment() {

    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!


    private val playlistId: Long by lazy {
        requireArguments().getLong(ARG_PLAYLIST_ID)
    }

    private val viewModel: PlaylistViewModel by viewModel {
        parametersOf(playlistId)
    }

    companion object {
        private const val ARG_PLAYLIST_ID = "playlist_id"

        fun newInstance(playlistId: Long): PlaylistFragment {
            return PlaylistFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_PLAYLIST_ID, playlistId)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun observeViewModel() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            renderState(state)
        }
    }

    private fun renderState(state: PlaylistState) {
        when (state) {
            is PlaylistState.Loading -> {
                // No special loading UI required for now
            }
            is PlaylistState.Content -> {
                showContent(state.playlist, state.tracks, state.totalDurationMinutes)
            }
        }
    }

    private fun showContent(
        playlist: Playlist,
        tracks: List<Track>,
        totalDurationMinutes: String
    ) {
        val header = binding.playlistHeader

        header.playlistNameText.text = playlist.name

        if (!playlist.description.isNullOrBlank()) {
            header.playlistDescriptionText.visibility = View.VISIBLE
            header.playlistDescriptionText.text = playlist.description
        } else {
            header.playlistDescriptionText.visibility = View.GONE
        }

        header.playlistDurationText.text = getString(
            R.string.minutes_format,
            totalDurationMinutes.toIntOrNull() ?: 0
        )

        header.playlistTracksCountText.text = getString(
            R.string.tracks_count_format,
            playlist.tracksCount
        )

        if (playlist.coverImagePath != null) {
            Glide.with(this)
                .load(playlist.coverImagePath)
                .apply(RequestOptions().placeholder(R.drawable.ic_no_dowload_album_312x312))
                .centerCrop()
                .into(header.playlistCoverImage)
        } else {
            header.playlistCoverImage.setImageResource(R.drawable.ic_no_dowload_album_312x312)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}