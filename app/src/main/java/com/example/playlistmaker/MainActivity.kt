package com.example.playlistmaker

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchBut = findViewById<Button>(R.id.search_button)

        val buttonClickListener: View.OnClickListener = object : View.OnClickListener{
            override fun onClick(v: View?) {
                Toast.makeText(this@MainActivity, "Нажали на кнопку поиска!", Toast.LENGTH_SHORT).show()
            }
        }
        searchBut.setOnClickListener (buttonClickListener)

        val mediaBut = findViewById<Button>(R.id.media_button)
        mediaBut.setOnClickListener {
            Toast.makeText(this@MainActivity,"Нажали на кнопку медиатеки!", Toast.LENGTH_SHORT).show()
        }

        val settingBut = findViewById<Button>(R.id.setting_button)
        settingBut.setOnClickListener {
            Toast.makeText(this@MainActivity,"Нажали на кнопку настройки!", Toast.LENGTH_SHORT).show()
        }
    }
}