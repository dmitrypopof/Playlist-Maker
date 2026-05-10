package com.example.playlistmaker.viewholders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.Track

class TrackViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val trackNameView: TextView
    private val artistNameView: TextView
    private val trackTimeView: TextView
    private val imageView: ImageView

    init {
        trackNameView = itemView.findViewById(R.id.track_name)
        artistNameView = itemView.findViewById(R.id.artist_name)
        trackTimeView = itemView.findViewById(R.id.track_time)
        imageView = itemView.findViewById(R.id.image)
    }

    fun bind(model: Track){
        trackNameView.text = model.trackName
        artistNameView.text = model.artistName
        trackTimeView.text = model.trackTime

        Glide.with(imageView.context)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.ic_placeholder_no_dowload)
            .error(R.drawable.ic_placeholder_no_dowload)
            .centerCrop()
            .into(imageView)
    }

}