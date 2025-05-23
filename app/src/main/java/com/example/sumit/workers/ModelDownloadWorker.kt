package com.example.sumit.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.sumit.utils.MODELS_PATH
import com.example.sumit.utils.MODEL_NAME
import com.google.android.gms.common.moduleinstall.ModuleInstall
import com.google.android.gms.common.moduleinstall.ModuleInstallRequest
import com.google.firebase.storage.FirebaseStorage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await
import java.io.File

private const val TAG = "ModelDownloadWorker"

/**
 * Worker to download the required models for processing from the cloud.
 */
class ModelDownloadWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    private val modelStoragePath = "models/$MODEL_NAME"

    override suspend fun doWork(): Result {
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val moduleInstallClient = ModuleInstall.getClient(applicationContext)
        val moduleInstallRequest = ModuleInstallRequest.newBuilder()
            .addApi(recognizer)
            .build()

        try {
            val installResponse =
                moduleInstallClient.installModules(moduleInstallRequest).await()
            if (installResponse.areModulesAlreadyInstalled()) {
                Log.d(TAG, "Text recognizer available")
            } else {
                Log.d(TAG, "Text recognizer downloaded")
            }
        } catch (throwable: Throwable) {
            if (throwable is CancellationException) {
                throw throwable
            }
            Log.e(TAG, "Text recognizer availability check failed", throwable)
            return Result.failure()
        }

        val storage = FirebaseStorage.getInstance()
        val modelRef = storage.getReference(modelStoragePath)

        val modelsDir = File(applicationContext.filesDir, MODELS_PATH)
        if (!modelsDir.exists()) {
            modelsDir.mkdirs()
        }

        val modelFile = File(modelsDir, MODEL_NAME)
        if (modelFile.exists()) {
            Log.d(TAG, "Model already available")
            return Result.success()
        }

        try {
            modelRef.getFile(modelFile).await()
            Log.d(TAG, "Model successfully downloaded")
        } catch (throwable: Throwable) {
            if (throwable is CancellationException) {
                throw throwable
            }
            Log.e(TAG, "Model download failed", throwable)
            return Result.failure()
        }

        return Result.success()
    }
}