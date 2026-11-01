package com.example.playlistmaker.feature.media.presentation.playlists

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class GridSpacingItemDecoration(
    private val spanCount: Int,
    private val spacing: Int,
    private val edgeSpacing: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val column = position % spanCount

        outRect.left = edgeSpacing + (column * spacing / spanCount)
        outRect.right = edgeSpacing + ((spanCount - 1 - column) * spacing / spanCount)

        if (position < spanCount) {
            outRect.top = edgeSpacing
        }
        outRect.bottom = spacing
    }
}