package com.example.sumit

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.example.sumit.ui.SumItApp
import com.example.sumit.ui.theme.SumItTheme
import com.example.sumit.utils.LANGUAGE_EXTRA_NAME
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Locale

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val language =
            savedInstanceState?.getString(LANGUAGE_EXTRA_NAME) ?: Locale.getDefault().language
        val app = applicationContext as SumItApplication
        Log.d(TAG, "Setting language to $language")

        val locale = Locale(language)
        Locale.setDefault(locale)

        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        createConfigurationContext(config)
        resources.updateConfiguration(config, resources.displayMetrics)

        lifecycleScope.launch {
            val syncRequested = app.container.preferencesRepository.syncPreference.first()

            if (syncRequested) {
                Log.d(TAG, "Syncing notes requested")
                val notesRepository = app.container.remoteNotesRepository
                notesRepository.startSync()
            }
        }

        enableEdgeToEdge()
        setContent {
            SumItTheme {
                SumItApp()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        runBlocking {
            val app = applicationContext as SumItApplication
            val language = app.container.preferencesRepository.langPreference.first()
            outState.putString(LANGUAGE_EXTRA_NAME, language)
        }
    }
}
