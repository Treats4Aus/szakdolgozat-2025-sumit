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
            "Your task is to fix mistyped characters in the provided text. Fix every word with the help of the context so in the end the text makes the most sense and is grammatically correct. Use every word from the input in the provided order, don't leave anything out. Pay attention to punctuation. Only output the resulting text. The input text is: $extractedText"

        return withContext(Dispatchers.IO) {
            val result = inferenceModel.generateOneTimeResponse(prompt)
            Log.d(TAG, result)
            Result.success()
        }
    }
}
