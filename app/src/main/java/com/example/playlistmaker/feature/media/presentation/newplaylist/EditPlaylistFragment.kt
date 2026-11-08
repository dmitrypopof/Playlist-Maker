package com.example.playlistmaker.feature.media.presentation.newplaylist

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class EditPlaylistFragment : NewPlaylistFragment() {

    private val playlistId: Long by lazy {
        requireArguments().getLong(ARG_PLAYLIST_ID)
    }

    override val viewModel: EditPlaylistViewModel by viewModel {
        parametersOf(playlistId)
    }

    companion object {
        private const val ARG_PLAYLIST_ID = "playlist_id"

        fun createArgs(playlistId: Long): Bundle {
            return Bundle().apply {
                putLong(ARG_PLAYLIST_ID, playlistId)
            }
        }
    }

    override fun getScreenTitleRes(): Int = R.string.edit_playlist_title
    override fun getButtonTextRes(): Int = R.string.save_playlist_button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.initialData.observe(viewLifecycleOwner) { playlist ->
            binding.nameEditText.setText(playlist.name)
            binding.descriptionEditText.setText(playlist.description.orEmpty())

            if (playlist.coverImagePath != null) {
                binding.coverImage.scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
                Glide.with(this)
                    .load(playlist.coverImagePath)
                    .centerCrop()
                    .into(binding.coverImage)
            }
        }
    }

    override fun handleBackPressed() {
        findNavController().navigateUp()
    }

    override fun onPlaylistSaved(playlistName: String) {
    }
}