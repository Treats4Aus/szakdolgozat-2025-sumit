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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            val app = applicationContext as SumItApplication
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
}
