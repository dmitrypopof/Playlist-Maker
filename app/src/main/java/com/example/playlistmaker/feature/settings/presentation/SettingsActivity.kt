package com.example.playlistmaker.feature.settings.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Инициализация ViewModel
        viewModel = ViewModelProvider(
            this,
            SettingsViewModelFactory(
                Creator.provideGetThemeSettingsUseCase(),
                Creator.provideUpdateThemeSettingsUseCase()
            )
        )[SettingsViewModel::class.java]

        // Настройка слушателей
        setupListeners()

        // Наблюдение за состоянием
        observeState()
    }

    private fun setupListeners() {
        binding.backButton.setOnClickListener {
            finish()
        }

        // Обработка переключателя темы
        binding.themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            // Передаем событие в ViewModel
            viewModel.onThemeChanged(isChecked)
            // Применяем тему
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) {
                    AppCompatDelegate.MODE_NIGHT_YES
                } else {
                    AppCompatDelegate.MODE_NIGHT_NO
                }
            )
        }

        // Кнопка "Поделиться приложением"
        binding.shareApp.setOnClickListener {
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

        // Кнопка "Написать в поддержку"
        binding.writeSupport.setOnClickListener {
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

        // Кнопка "Пользовательское соглашение"
        binding.userAgreement.setOnClickListener {
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

    private fun observeState() {
        viewModel.state.observe(this) { state ->
            renderState(state)
        }
    }

    private fun renderState(state: SettingsState) {
        when (state) {
            is SettingsState.ThemeSettings -> {
                // Обновляем состояние переключателя без вызова listener
                binding.themeSwitcher.setOnCheckedChangeListener(null)
                binding.themeSwitcher.isChecked = state.isDarkTheme
                binding.themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
                    viewModel.onThemeChanged(isChecked)
                    AppCompatDelegate.setDefaultNightMode(
                        if (isChecked) {
                            AppCompatDelegate.MODE_NIGHT_YES
                        } else {
                            AppCompatDelegate.MODE_NIGHT_NO
                        }
                    )
                }
            }
        }
    }
}