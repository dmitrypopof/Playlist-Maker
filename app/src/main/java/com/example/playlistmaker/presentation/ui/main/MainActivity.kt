package com.example.playlistmaker.presentation.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.playlistmaker.databinding.ActivityMainBinding
import com.example.playlistmaker.presentation.ui.media.MediaActivity
import com.example.playlistmaker.feature.search.presentation.SearchActivity
import com.example.playlistmaker.feature.settings.presentation.SettingsActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(top = systemBars.top,
                bottom = systemBars.bottom)
            insets
        }

        val buttonClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                //Toast.makeText(this@MainActivity, "Нажали на кнопку поиска!", Toast.LENGTH_SHORT).show()
                val searchIntent = Intent(this@MainActivity, SearchActivity::class.java)
                startActivity(searchIntent)
            }
        }
        binding.searchButton.setOnClickListener(buttonClickListener)

        binding.mediaButton.setOnClickListener {
            //Toast.makeText(this@MainActivity,"Нажали на кнопку медиатеки!", Toast.LENGTH_SHORT).show()
            val mediaIntent = Intent(this, MediaActivity::class.java)
            startActivity(mediaIntent)
        }

        binding.settingButton.setOnClickListener {
            //Toast.makeText(this@MainActivity,"Нажали на кнопку настройки!", Toast.LENGTH_SHORT).show()
            val setIntent = Intent(this, SettingsActivity::class.java)
            startActivity(setIntent)
        }
    }
}