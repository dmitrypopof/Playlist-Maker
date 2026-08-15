package com.example.playlistmaker.feature.search.presentation

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.ItemTrackBinding
import com.example.playlistmaker.feature.search.domain.model.Track

class TrackAdapter(
    private var tracks: List<Track>,
    private val onTrackClick: ((Track) -> Unit)? = null
) : RecyclerView.Adapter<TrackViewHolder>() {

    // Для debounce кликов
    private var isClickAllowed = true
    private val clickHandler = Handler(Looper.getMainLooper())
    private val CLICK_DEBOUNCE_DELAY = 1000L

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TrackViewHolder {
        val binding = ItemTrackBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: TrackViewHolder,
        position: Int
    ) {

        val track = tracks[position]
        holder.bind(track)

        // Добавляем обработчик клика
        holder.itemView.setOnClickListener {
            if (clickDebounce()) {


                // Сначала вызываем колбэк для сохранения в историю
                onTrackClick?.invoke(track)
                // Затем открываем AudioPlayer
//                val context = holder.itemView.context
//                TrackIntentHelper.startAudioPlayer(context, track)
            }
        }
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateTracks(newTracks: List<Track>) {
        tracks = newTracks
        notifyDataSetChanged()
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            clickHandler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }
}