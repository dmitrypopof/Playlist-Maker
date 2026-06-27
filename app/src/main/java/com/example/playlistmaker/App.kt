package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate


class App: Application() {

    companion object {
        const val THEME_PREF_KEY = "theme_enabled"
    }

    var darkTheme = false

    override fun onCreate() {
        super.onCreate()
        val prefs = getSharedPreferences("app_settings", MODE_PRIVATE)
        darkTheme = prefs.getBoolean(THEME_PREF_KEY, false)

        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled

        val prefs = getSharedPreferences("app_settings", MODE_PRIVATE)
        prefs.edit()
            .putBoolean(THEME_PREF_KEY, darkThemeEnabled)
            .apply()

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}