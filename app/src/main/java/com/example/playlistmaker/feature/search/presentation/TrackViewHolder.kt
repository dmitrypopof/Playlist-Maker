package com.example.playlistmaker.feature.search.presentation

import android.text.TextUtils
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ItemTrackBinding
import com.example.playlistmaker.feature.search.domain.model.Track

class TrackViewHolder(private val binding: ItemTrackBinding) : RecyclerView.ViewHolder(binding.root)  {

    fun bind(model: Track){
        binding.trackName.text = model.trackName
        binding.trackName.maxLines = 1
        binding.trackName.ellipsize = TextUtils.TruncateAt.END

        binding.artistName.text = model.artistName
        binding.trackTime.text = model.formattedTime

        Glide.with(binding.image.context)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.ic_placeholder_no_download_45x45)
            .error(R.drawable.ic_placeholder_no_download_45x45)
            .centerCrop()
            .into(binding.image)
    }

}