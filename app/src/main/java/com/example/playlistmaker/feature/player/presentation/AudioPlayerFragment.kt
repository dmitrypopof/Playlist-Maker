package com.example.playlistmaker.feature.player.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentAudioplayerBinding
import com.example.playlistmaker.feature.search.domain.model.Track
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel

class AudioPlayerFragment : Fragment() {

    companion object {
        private const val ARG_TRACK_JSON = "track_json"

        fun createArgs(track: Track): Bundle {
            val json = Gson().toJson(track)
            return bundleOf(ARG_TRACK_JSON to json)
        }
    }

    private var _binding: FragmentAudioplayerBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AudioPlayerViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAudioplayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Получаем данные о треке из аргументов фрагмента вместо Intent
        val track = getTrackFromArgs()

        setupListeners()
        observeState()

        viewModel.loadTrack(track)
        displayTrackInfo(track)
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getTrackFromArgs(): Track {
        val json = requireArguments().getString(ARG_TRACK_JSON)
        return if (json != null) {
            try {
                Gson().fromJson(json, Track::class.java) ?: Track.createDefault()
            } catch (e: Exception) {
                Track.createDefault()
            }
        } else {
            Track.createDefault()
        }
    }

    private fun displayTrackInfo(track: Track) {
        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName

        val imageView = binding.albumCover.getChildAt(0) as AppCompatImageView
        Glide.with(this)
            .load(
                track.artworkUrl100.replace(
                    "100x100",
                    "512x512"
                )
            )
            .placeholder(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_placeholder_no_download_45x45
                )
            )
            .error(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_placeholder_no_download_45x45
                )
            )
            .centerCrop()
            .into(imageView)

        binding.apply {
            durationValue.text = track.formattedTime
            albumValue.text = track.collectionName
            yearValue.text = track.displayYear
            genreValue.text = track.primaryGenreName
            countryValue.text = track.country
        }

        binding.trackTime.text = track.formattedTime
    }

    private fun setupListeners() {
        binding.backButton.setOnClickListener {
            // Навигация через NavController вместо onBackPressedDispatcher
            findNavController().navigateUp()
        }

        binding.playButton.setOnClickListener {
            viewModel.playbackControl()
        }
    }

    private fun observeState() {
        // Используем viewLifecycleOwner вместо this (Activity)
        viewModel.state.observe(viewLifecycleOwner) { state ->
            renderState(state)
        }
    }

    private fun renderState(state: AudioPlayerState) {
        when (state) {
            is AudioPlayerState.Default -> {
                binding.playButton.setIconResource(R.drawable.ic_play_button)
            }
            is AudioPlayerState.Content -> {
                binding.playButton.setIconResource(R.drawable.ic_play_button)
            }
            is AudioPlayerState.Prepared -> {
                binding.playButton.setIconResource(R.drawable.ic_play_button)
            }
            is AudioPlayerState.Playing -> {
                binding.playButton.setIconResource(R.drawable.ic_pause_button)
            }
            is AudioPlayerState.Paused -> {
                binding.playButton.setIconResource(R.drawable.ic_play_button)
            }
            is AudioPlayerState.Progress -> {
                binding.trackTime.text = state.currentPosition
            }
        }
    }
}