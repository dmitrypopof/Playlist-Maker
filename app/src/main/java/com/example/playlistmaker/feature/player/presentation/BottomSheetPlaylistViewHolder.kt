package com.example.playlistmaker.feature.player.presentation

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ItemPlaylistBottomSheetBinding
import com.example.playlistmaker.feature.media.domain.model.Playlist

class BottomSheetPlaylistViewHolder(
    private val binding: ItemPlaylistBottomSheetBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(playlist: Playlist) {
        binding.playlistNameText.text = playlist.name
        binding.playlistTracksCountText.text = binding.root.context.getString(
            R.string.tracks_count_format,
            playlist.tracksCount
        )

        if (playlist.coverImagePath != null) {
            Glide.with(binding.root)
                .load(playlist.coverImagePath)
                .apply(RequestOptions().placeholder(R.drawable.ic_placeholder_no_download_45x45))
                .centerCrop()
                .into(binding.playlistCoverImage)
        } else {
            binding.playlistCoverImage.setImageResource(R.drawable.ic_placeholder_no_download_45x45)
        }
    }
}