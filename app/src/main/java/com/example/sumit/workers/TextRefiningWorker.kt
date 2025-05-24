package com.example.sumit.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.sumit.SumItApplication
import com.example.sumit.utils.KEY_EXTRACTED_TEXT
import com.example.sumit.utils.KEY_NOTE_TEXT
import com.example.sumit.utils.KEY_PAGE_COUNT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "TextRefiningWorker"

/**
 * Worker to use LLM inference to fix spelling errors in the provided text.
 */
class TextRefiningWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        val pageCount = inputData.getInt(KEY_PAGE_COUNT, 1)
        val extractedText = List(pageCount) { it }
            .map { inputData.getString(KEY_EXTRACTED_TEXT.format(it)) }
            .joinToString(" ")
        Log.d(TAG, "Extracted text: $extractedText")
        val prompt = """
            You are an expert at proofreading and correcting text. Your task is to fix mistyped or misplaced letters in the following text while preserving the original meaning and structure. Correct typos and ensure proper spelling without altering the intended content. The text is in English. Here's the text:
            `$extractedText`
            Please provide the corrected version. Output only raw text.
            
            **Example Input:**
            "The qick brown fox jmps ovre the lozy dog."

            **Example Output:**
            "The quick brown fox jumps over the lazy dog."
        """.trimIndent()

        return withContext(Dispatchers.IO) {
            val app = applicationContext as SumItApplication
            val inferenceModel = app.container.inferenceModel
            val result = inferenceModel.generateOneTimeResponse(prompt)
            Log.d(TAG, result)
            Result.success(workDataOf(KEY_NOTE_TEXT to result))
        }
    }
}
