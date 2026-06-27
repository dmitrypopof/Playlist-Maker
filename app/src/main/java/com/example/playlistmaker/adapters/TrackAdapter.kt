package com.example.playlistmaker.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.models.Track
import com.example.playlistmaker.viewholders.TrackViewHolder

class TrackAdapter(
    private val tracks: List<Track>,
    private val onTrackClick: ((Track) -> Unit)? = null  // Добавляем слушатель кликов
) : RecyclerView.Adapter<TrackViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TrackViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_track, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: TrackViewHolder,
        position: Int
    ) {

        val track = tracks[position]
        holder.bind(track)

        // Добавляем обработчик клика
        holder.itemView.setOnClickListener {
            onTrackClick?.invoke(track)
        }
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

}