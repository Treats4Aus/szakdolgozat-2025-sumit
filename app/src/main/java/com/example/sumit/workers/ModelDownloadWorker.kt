package com.example.sumit.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.android.gms.common.moduleinstall.ModuleInstall
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await

private const val TAG = "ModelDownloadWorker"

class ModelDownloadWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val moduleInstallClient = ModuleInstall.getClient(applicationContext)
        try {
            val result = moduleInstallClient.areModulesAvailable(recognizer).await()
            if (result.areModulesAvailable()) {
                Log.d(TAG, "Text recognizer available")
                return Result.success()
            } else {
                Log.d(TAG, "Text recognizer not available")
                return Result.failure()
            }
        } catch (throwable: Throwable) {
            if (throwable is CancellationException) {
                throw throwable
            }
            Log.e(TAG, "Text recognizer availability check failed")
            return Result.failure()
        }
    }
}