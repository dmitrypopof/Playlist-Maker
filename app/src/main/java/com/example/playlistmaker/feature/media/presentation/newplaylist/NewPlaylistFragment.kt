package com.example.playlistmaker.feature.media.presentation.newplaylist

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel

class NewPlaylistFragment : Fragment() {

    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NewPlaylistViewModel by viewModel()

    private val pickMedia = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.onCoverSelected(uri)
        }
    }

    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            handleBackPressed()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backPressedCallback)

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.backButton.setOnClickListener {
            handleBackPressed()
        }

        binding.coverImage.setOnClickListener {
            pickMedia.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }

        binding.nameEditText.addTextChangedListener(
            onTextChanged = { text, _, _, _ ->
                viewModel.onNameChanged(text?.toString().orEmpty())
            }
        )

        binding.descriptionEditText.addTextChangedListener(
            onTextChanged = { text, _, _, _ ->
                viewModel.onDescriptionChanged(text?.toString().orEmpty())
            }
        )

        binding.createButton.setOnClickListener {
            viewModel.createPlaylist()
        }
    }

    private fun handleBackPressed() {
        if (viewModel.hasUnsavedData()) {
            showExitConfirmationDialog()
        } else {
            findNavController().navigateUp()
        }
    }

    private fun showExitConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.finish_playlist_creation_title)
            .setMessage(R.string.finish_playlist_creation_message)
            .setNegativeButton(R.string.cancel_button) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(R.string.finish_button) { dialog, _ ->
                dialog.dismiss()
                findNavController().navigateUp()
            }
            .show()
    }

    private fun observeViewModel() {
        viewModel.isCreateButtonEnabled.observe(viewLifecycleOwner) { isEnabled ->
            binding.createButton.isEnabled = isEnabled
        }

        viewModel.coverPreview.observe(viewLifecycleOwner) { uri ->
            showCover(uri)
        }

        viewModel.createdPlaylistEvent.observe(viewLifecycleOwner) { playlistName ->
            if (playlistName != null) {
                showPlaylistCreatedMessage(playlistName)
                viewModel.onCreatedEventHandled()
                findNavController().navigateUp()
            }
        }
    }

    private fun showCover(uri: Uri?) {
        if (uri != null) {
            binding.coverImage.scaleType = ImageView.ScaleType.CENTER_CROP
            Glide.with(this)
                .load(uri)
                .centerCrop()
                .into(binding.coverImage)
        } else {
            binding.coverImage.scaleType = ImageView.ScaleType.CENTER_INSIDE
            binding.coverImage.setImageResource(R.drawable.ic_placeholder_no_download_45x45)
        }
    }

    private fun showPlaylistCreatedMessage(playlistName: String) {
        val message = getString(R.string.playlist_created_message, playlistName)
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}