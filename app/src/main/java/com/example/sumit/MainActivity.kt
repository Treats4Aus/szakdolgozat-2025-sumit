package com.example.sumit

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.example.sumit.ui.SumItApp
import com.example.sumit.ui.theme.SumItTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    private lateinit var application: SumItApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            application = applicationContext as SumItApplication
            val syncRequested = application.container.preferencesRepository.syncPreference.first()

            if (syncRequested) {
                Log.d(TAG, "Syncing notes requested")
                val notesRepository = application.container.remoteNotesRepository
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

    override fun onDestroy() {
        super.onDestroy()

        val notesRepository = application.container.remoteNotesRepository
        notesRepository.cancelSync()
    }
}
