package com.example.sumit.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.sumit.utils.InferenceModel
import com.example.sumit.utils.KEY_EXTRACTED_TEXT
import com.example.sumit.utils.KEY_PAGE_COUNT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "TextRefiningWorker"

class TextRefiningWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    private val inferenceModel = InferenceModel.getInstance(applicationContext)

    override suspend fun doWork(): Result {
        val pageCount = inputData.getInt(KEY_PAGE_COUNT, 1)
        val extractedText = List(pageCount) { it }
            .map { inputData.getString(KEY_EXTRACTED_TEXT.format(it)) }
            .joinToString(" ")
        Log.d(TAG, "Extracted text: $extractedText")
        val prompt =
            "Your task is to count the number of words in the following text. Only output a single the number. The text is as follows: $extractedText"

        return withContext(Dispatchers.IO) {
            val result = inferenceModel.generateOneTimeResponse(prompt)
            Log.d(TAG, result)
            Result.success()
        }
    }
}
