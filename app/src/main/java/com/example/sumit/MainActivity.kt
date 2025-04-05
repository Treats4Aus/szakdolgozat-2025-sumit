package com.example.sumit

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.sumit.ui.SumItApp
import com.example.sumit.ui.theme.SumItTheme
import com.example.sumit.utils.OUTPUT_PATH
import java.io.File

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SumItTheme {
                SumItApp()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val cachedPhotosDir = File(cacheDir, OUTPUT_PATH)
        if (cachedPhotosDir.exists()) {
            Log.d(TAG, "Deleting cache")
            cachedPhotosDir.deleteRecursively()
        }
    }
}
