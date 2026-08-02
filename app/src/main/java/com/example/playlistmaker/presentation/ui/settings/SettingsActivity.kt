package com.example.playlistmaker.presentation.ui.settings

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    private val getThemeSettingsUseCase by lazy { Creator.provideGetThemeSettingsUseCase() }
    private val updateThemeSettingsUseCase by lazy { Creator.provideUpdateThemeSettingsUseCase() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.themeSwitcher.isChecked = getThemeSettingsUseCase()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.themeSwitcher.setOnCheckedChangeListener { _, checked ->
            updateThemeSettingsUseCase(checked)
            AppCompatDelegate.setDefaultNightMode(
                if (checked) {
                    AppCompatDelegate.MODE_NIGHT_YES
                } else {
                    AppCompatDelegate.MODE_NIGHT_NO
                }
            )
        }

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

        binding.userAgreement.setOnClickListener {
            val intent =
                Intent(Intent.ACTION_VIEW, getString(R.string.linkPracticumOffer).toUri())
            startActivity(
                Intent.createChooser(
                    intent,
                    getString(R.string.textBottomSheet_agreement)
                )
            )
        }


    }
}