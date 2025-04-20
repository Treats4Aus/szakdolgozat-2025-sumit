package com.example.sumit.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.sumit.utils.InferenceModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "TextRefiningWorker"

class TextRefiningWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    private val inferenceModel = InferenceModel.getInstance(applicationContext)

    override suspend fun doWork(): Result {
        val prompt = "Give me a list of 5 places to visit in Budapest."

        return withContext(Dispatchers.IO) {
            val result = inferenceModel.generateOneTimeResponse(prompt)
            Log.d(TAG, result)
            Result.success()
        }
    }
}
