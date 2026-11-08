package com.example.playlistmaker.feature.media.presentation.playlist

import com.example.playlistmaker.feature.media.domain.model.Playlist
import com.example.playlistmaker.feature.search.domain.model.Track

sealed class PlaylistEvent {

    // Запрос на шеринг плейлиста с треками
    data class Share(val playlist: Playlist, val tracks: List<Track>) : PlaylistEvent()

    // Попытка поделиться пустым плейлистом
    object ShareEmptyPlaylist : PlaylistEvent()

    // Плейлист был успешно удалён, то вернуться на экран Медиатека
    object PlaylistDeleted : PlaylistEvent()
}