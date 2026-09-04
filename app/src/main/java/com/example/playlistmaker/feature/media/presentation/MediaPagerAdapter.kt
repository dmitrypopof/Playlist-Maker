package com.example.playlistmaker.feature.media.presentation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.playlistmaker.feature.media.presentation.favorite.FavoriteTracksFragment
import com.example.playlistmaker.feature.media.presentation.playlists.PlaylistsFragment

class MediaPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            FAVORITE_TRACKS_POSITION -> FavoriteTracksFragment.newInstance()
            PLAYLISTS_POSITION -> PlaylistsFragment.newInstance()
            else -> throw IllegalArgumentException("Unknown position: $position")
        }
    }

    companion object {
        const val FAVORITE_TRACKS_POSITION = 0
        const val PLAYLISTS_POSITION = 1
    }
}