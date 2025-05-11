package com.example.sumit.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.sumit.SumItApplication
import com.example.sumit.utils.KEY_KEYWORDS_TEXT
import com.example.sumit.utils.KEY_NOTE_TEXT
import com.example.sumit.utils.KEY_SUMMARY_TEXT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "SummaryGeneratingWorker"

class SummaryGeneratingWorker(ctx: Context, params: WorkerParameters) :
    CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        val structuredText = inputData.getString(KEY_NOTE_TEXT)

        val summaryPrompt = """
            Your task is to write a short summary for the following text in 4 to 8 sentences. Use your own words but stay true to the original meaning. Don't leave out specifics such as numeric data, location names or people's names. Here is the text:
            `$structuredText`
            Please provide the summary for the given text on a single line. Don't output anything else. The text is in English.
        """.trimIndent()

        val keywordsPrompt = """
            Please provide up to 5 keywords that describe the previously provide text best. Output these keywords on a single line, separated by a comma. Don't output anything else.
        """.trimIndent()

        return withContext(Dispatchers.IO) {
            val app = applicationContext as SumItApplication
            val inferenceModel = app.container.inferenceModel

            val summary = inferenceModel.generateResponse(summaryPrompt)
            Log.d(TAG, "Summary: $summary")

            val keywords = inferenceModel.generateResponse(keywordsPrompt)
            Log.d(TAG, "Keywords: $keywords")

            inferenceModel.resetSession()

            Result.success(
                workDataOf(
                    KEY_NOTE_TEXT to structuredText,
                    KEY_SUMMARY_TEXT to summary,
                    KEY_KEYWORDS_TEXT to keywords
                )
            )
        }
    }
}