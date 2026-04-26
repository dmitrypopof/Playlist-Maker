package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(top = systemBars.top,
                bottom = systemBars.bottom)
            insets
        }

        val searchBut = findViewById<Button>(R.id.search_button)
        val mediaBut = findViewById<Button>(R.id.media_button)
        val settingBut = findViewById<Button>(R.id.setting_button)


        val buttonClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                //Toast.makeText(this@MainActivity, "Нажали на кнопку поиска!", Toast.LENGTH_SHORT).show()
                val searchIntent = Intent(this@MainActivity, SearchActivity::class.java)
                startActivity(searchIntent)
            }
        }
        searchBut.setOnClickListener(buttonClickListener)


        mediaBut.setOnClickListener {
            //Toast.makeText(this@MainActivity,"Нажали на кнопку медиатеки!", Toast.LENGTH_SHORT).show()
            val mediaIntent = Intent(this, MediaActivity::class.java)
            startActivity(mediaIntent)
        }


        settingBut.setOnClickListener {
            //Toast.makeText(this@MainActivity,"Нажали на кнопку настройки!", Toast.LENGTH_SHORT).show()
            val setIntent = Intent(this, SettingsActivity::class.java)
            startActivity(setIntent)
        }


    }
}