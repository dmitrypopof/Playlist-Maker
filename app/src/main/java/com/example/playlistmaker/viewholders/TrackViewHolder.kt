package com.example.playlistmaker.viewholders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.models.Track

class TrackViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)  {
    private val trackNameView: TextView by lazy { itemView.findViewById(R.id.track_name) }
    private val artistNameView: TextView by lazy { itemView.findViewById(R.id.artist_name) }
    private val trackTimeView: TextView by lazy { itemView.findViewById(R.id.track_time) }
    private val imageView: ImageView by lazy { itemView.findViewById(R.id.image) }

    fun bind(model: Track){
        trackNameView.text = model.trackName
        artistNameView.text = model.artistName
        trackTimeView.text = model.trackTime

        Glide.with(imageView.context)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.ic_placeholder_no_download_45x45)
            .error(R.drawable.ic_placeholder_no_download_45x45)
            .centerCrop()
            .into(imageView)
    }

}