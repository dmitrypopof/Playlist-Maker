package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val backButton = findViewById<MaterialButton>(R.id.back_button)
        val shareButton = findViewById<MaterialTextView>(R.id.shareApp)
        val supButton = findViewById<MaterialTextView>(R.id.writeSupport)
        val agreementButton = findViewById<MaterialTextView>(R.id.userAgreement)


        backButton.setOnClickListener {
            finish()
        }

        shareButton.setOnClickListener {
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

        supButton.setOnClickListener {
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

        agreementButton.setOnClickListener {
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