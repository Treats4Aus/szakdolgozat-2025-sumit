package com.example.sumit.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.sumit.SumItApplication
import com.example.sumit.utils.KEY_NOTE_TEXT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "TextStructuringWorker"

class TextStructuringWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        val refinedText = inputData.getString(KEY_NOTE_TEXT)

        val prompt = """
            You are an expert at restructuring text. Your task is to divide the text into logically connected blocks, each consisting of 2 to 5 sentences. Output each block on a separate line. Keep the original wording. The first line should be a title, which represents the text's content in a few words. Here is the text:
            `$refinedText`
            Please provide the structured lines of text. Don't output anything else. The input text is in English.
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