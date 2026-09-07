package com.example.playlistmaker.feature.settings.presentation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        observeState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupListeners() {
        // Кнопка "Назад" удалена — она больше не нужна на этом экране

        binding.themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onThemeChanged(isChecked)
            applyTheme(isChecked)
        }

        binding.shareApp.setOnClickListener {
            shareApp()
        }

        binding.writeSupport.setOnClickListener {
            writeSupport()
        }

        binding.userAgreement.setOnClickListener {
            openUserAgreement()
        }
    }

    private fun observeState() {
        // Используем viewLifecycleOwner вместо this (Activity)
        viewModel.state.observe(viewLifecycleOwner) { state ->
            renderState(state)
        }
    }

    private fun renderState(state: SettingsState) {
        when (state) {
            is SettingsState.ThemeSettings -> {
                binding.themeSwitcher.setOnCheckedChangeListener(null)
                binding.themeSwitcher.isChecked = state.isDarkTheme
                binding.themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
                    viewModel.onThemeChanged(isChecked)
                    applyTheme(isChecked)
                }
            }
        }
    }

    private fun applyTheme(isDarkTheme: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    private fun shareApp() {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.link_androidDeveloper))
        startActivity(
            Intent.createChooser(
                intent,
                getString(R.string.textBottomSheet_shareApp)
            )
        )
    }

    private fun writeSupport() {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = "mailto:".toUri()
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.sendMail)))
        intent.putExtra(
            Intent.EXTRA_SUBJECT,
            getString(R.string.subjectMail)
        )
        intent.putExtra(
            Intent.EXTRA_TEXT,
            getString(R.string.textMail)
        )
        startActivity(Intent.createChooser(intent, getString(R.string.textBottomSheet_support)))
    }

    private fun openUserAgreement() {
        val intent = Intent(
            Intent.ACTION_VIEW,
            getString(R.string.linkPracticumOffer).toUri()
        )
        startActivity(
            Intent.createChooser(
                intent,
                getString(R.string.textBottomSheet_agreement)
            )
        )
    }
}