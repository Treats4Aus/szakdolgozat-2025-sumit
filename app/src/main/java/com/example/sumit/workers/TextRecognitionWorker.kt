package com.example.sumit.workers

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.sumit.utils.KEY_EXTRACTED_TEXT
import com.example.sumit.utils.KEY_PHOTO_INDEX
import com.example.sumit.utils.KEY_PHOTO_URI
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.IOException

private const val TAG = "TextRecognitionWorker"

class TextRecognitionWorker(ctx: Context, param: WorkerParameters) : CoroutineWorker(ctx, param) {
    override suspend fun doWork(): Result {
        val photoIndex = inputData.getInt(KEY_PHOTO_INDEX, 0)
        val photoUri = inputData.getString(KEY_PHOTO_URI)

        return withContext(Dispatchers.IO) {
            return@withContext try {
                require(!photoUri.isNullOrBlank()) {
                    val errorMessage = "Invalid input uri"
                    Log.e(TAG, errorMessage)
                    errorMessage
                }
                val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                val image = InputImage.fromFilePath(applicationContext, Uri.parse(photoUri))

                val recognitionResult = recognizer.process(image).await()

                for (block in recognitionResult.textBlocks) {
                    for (line in block.lines) {
                        Log.d(TAG, line.text)
                    }
                }
                val outputData =
                    workDataOf(
                        KEY_EXTRACTED_TEXT.format(photoIndex) to recognitionResult.text.replace(
                            "\n",
                            " "
                        )
                    )

                Result.success(outputData)
            } catch (ioException: IOException) {
                Log.e(TAG, "Loading photo failed", ioException)
                Result.failure()
            } catch (throwable: Throwable) {
                if (throwable is CancellationException) {
                    throw throwable
                }
                Log.e(TAG, "Error extracting text: ${throwable.message}", throwable)
                Result.failure()
            }
        }
    }
}
