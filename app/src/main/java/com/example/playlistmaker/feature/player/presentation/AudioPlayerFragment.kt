package com.example.playlistmaker.feature.player.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentAudioplayerBinding
import com.example.playlistmaker.feature.search.domain.model.Track
import com.google.android.material.bottomsheet.BottomSheetBehavior
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

    private var bottomSheetAdapter: BottomSheetPlaylistAdapter? = null
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

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
        setupBottomSheet()
        setupBottomSheetAdapter()
        setupListeners()
        observeState()
        observeFavoriteState()
        observePlaylists()
        observeAddToPlaylistResult()

        viewModel.loadTrack(track)
        displayTrackInfo(track)
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bottomSheetAdapter = null
        binding.bottomSheetPlaylistsRecyclerView.adapter = null
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

    private fun setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                    }
                    else -> {
                        binding.overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.overlay.alpha = (slideOffset + 1f) / 2f
            }
        })
    }

    private fun setupBottomSheetAdapter() {
        bottomSheetAdapter = BottomSheetPlaylistAdapter(emptyList()) { playlist ->
            viewModel.onPlaylistSelected(playlist)
        }

        binding.bottomSheetPlaylistsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = bottomSheetAdapter
        }
    }

    private fun setupListeners() {
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.playButton.setOnClickListener {
            viewModel.playbackControl()
        }

        binding.favouriteButton.setOnClickListener {
            viewModel.onFavoriteClicked()
        }

        // открытие Bottom Sheet со списком плейлистов
        binding.addPlaylist.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        // переход на экран создания плейлиста
        binding.newPlaylistInBottomSheetButton.setOnClickListener {
            findNavController().navigate(R.id.action_audioPlayerFragment_to_newPlaylistFragment)
        }
    }

    private fun observeState() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            renderState(state)
        }
    }

    private fun observeFavoriteState() {
        viewModel.isFavorite.observe(viewLifecycleOwner) { isFavorite ->
            renderFavoriteState(isFavorite)
        }
    }

    // подписка на список плейлистов
    private fun observePlaylists() {
        viewModel.playlists.observe(viewLifecycleOwner) { playlists ->
            bottomSheetAdapter?.updatePlaylists(playlists)
        }
    }

    //  подписка на результат добавления трека в плейлист
    private fun observeAddToPlaylistResult() {
        viewModel.addToPlaylistResult.observe(viewLifecycleOwner) { result ->
            if (result != null) {
                handleAddToPlaylistResult(result)
                viewModel.onAddToPlaylistResultHandled()
            }
        }
    }

    // ADDED: обработка результата — Toast + закрытие Bottom Sheet при успехе
    private fun handleAddToPlaylistResult(result: AudioPlayerAddToPlaylistResult) {
        when (result) {
            is AudioPlayerAddToPlaylistResult.Added -> {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                val message = getString(R.string.added_to_playlist_message, result.playlistName)
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
            is AudioPlayerAddToPlaylistResult.AlreadyAdded -> {
                val message = getString(R.string.track_already_in_playlist_message, result.playlistName)
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun renderFavoriteState(isFavorite: Boolean) {
        val iconRes = if (isFavorite) {
            R.drawable.ic_favourites_button_active
        } else {
            R.drawable.ic_favourites_button
        }
        binding.favouriteButton.setIconResource(iconRes)
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