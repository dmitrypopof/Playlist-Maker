package com.example.playlistmaker.feature.search.presentation

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.ItemTrackBinding
import com.example.playlistmaker.feature.search.domain.model.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TrackAdapter(
    private var tracks: List<Track>,
    private val lifecycleOwner: LifecycleOwner,
    private val onTrackClick: ((Track) -> Unit)? = null
) : RecyclerView.Adapter<TrackViewHolder>() {

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
    // Для debounce кликов
    private var isClickAllowed = true
    private var clickJob: Job? = null

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
            clickJob = lifecycleOwner.lifecycleScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }
}