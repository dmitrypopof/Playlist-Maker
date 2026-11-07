package com.example.playlistmaker.feature.media.presentation.playlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.feature.media.domain.model.Playlist
import com.example.playlistmaker.feature.player.presentation.AudioPlayerFragment
import com.example.playlistmaker.feature.search.domain.model.Track
import com.example.playlistmaker.feature.search.presentation.TrackAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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

    // Адаптер списка треков плейлиста
    private var tracksAdapter: TrackAdapter? = null

    // Поведение Bottom Sheet для списка треков
    private lateinit var tracksBottomSheetBehavior: BottomSheetBehavior<LinearLayout>

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

        setupBottomSheet()
        setupTracksAdapter()
        setupListeners()
        observeViewModel()
    }

    // Настройка Bottom Sheet со списком треков
    private fun setupBottomSheet() {
        tracksBottomSheetBehavior = BottomSheetBehavior.from(binding.playlistTracksBottomSheet).apply {
            isHideable = false
            state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun setupTracksAdapter() {
        tracksAdapter = TrackAdapter(
            tracks = emptyList(),
            lifecycleOwner = viewLifecycleOwner,
            onTrackClick = { track ->
                openAudioPlayer(track)
            },
            onTrackLongClick = { track ->
                showDeleteTrackDialog(track)
            }
        )

        binding.playlistTracksRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = tracksAdapter
        }
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

        tracksAdapter?.updateTracks(tracks)
    }

    private fun openAudioPlayer(track: Track) {
        findNavController().navigate(
            R.id.action_playlistFragment_to_audioPlayerFragment,
            AudioPlayerFragment.createArgs(track)
        )
    }

    private fun showDeleteTrackDialog(track: Track) {
        MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogButtonTheme)
            .setMessage(R.string.delete_track_dialog_message)
            .setNegativeButton(R.string.no_button) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(R.string.yes_button) { dialog, _ ->
                dialog.dismiss()
                viewModel.removeTrack(track)
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tracksAdapter = null
        binding.playlistTracksRecyclerView.adapter = null
        _binding = null
    }
}