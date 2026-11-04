package com.example.playlistmaker.feature.media.presentation.playlists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.feature.media.domain.model.Playlist
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaylistsViewModel by viewModel()

    private var adapter: PlaylistAdapter? = null

    companion object {
        private const val SPAN_COUNT = 2

        fun newInstance(): PlaylistsFragment = PlaylistsFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()
        setupListeners()
        observeState()
    }

    private fun setupAdapter() {
        adapter = PlaylistAdapter(emptyList()) { playlist ->
            onPlaylistClicked(playlist)
        }

        binding.playlistsRecyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), SPAN_COUNT)
            adapter = this@PlaylistsFragment.adapter
        }
    }

    private fun setupListeners() {
        binding.newPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.action_mediaFragment_to_newPlaylistFragment)
        }
    }

    private fun observeState() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            renderState(state)
        }
    }

    private fun renderState(state: PlaylistsState) {
        when (state) {
            is PlaylistsState.Empty -> showEmpty()
            is PlaylistsState.Content -> showContent(state.playlists)
        }
    }

    private fun showEmpty() {
        binding.playlistsRecyclerView.visibility = View.GONE
        binding.placeholderImage.visibility = View.VISIBLE
        binding.placeholderText.visibility = View.VISIBLE
    }

    private fun showContent(playlists: List<Playlist>) {
        binding.playlistsRecyclerView.visibility = View.VISIBLE
        binding.placeholderImage.visibility = View.GONE
        binding.placeholderText.visibility = View.GONE
        adapter?.updatePlaylists(playlists)
    }

    private fun onPlaylistClicked(playlist: Playlist) {
        // Переход на экран плейлиста будет реализован в следующем спринте
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
        binding.playlistsRecyclerView.adapter = null
        _binding = null
    }
}