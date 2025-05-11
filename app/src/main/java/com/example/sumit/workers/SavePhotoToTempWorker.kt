package com.example.sumit.workers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.sumit.utils.KEY_PHOTO_INDEX
import com.example.sumit.utils.KEY_PHOTO_URI
import com.example.sumit.utils.PHOTO_TYPE_TEMP
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "SavePhotoToTempWorker"

class SavePhotoToTempWorker(ctx: Context, params: WorkerParameters) :
    CoroutineWorker(ctx, params) {
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
                val resolver = applicationContext.contentResolver

                var photo: Bitmap?
                resolver.openInputStream(Uri.parse(photoUri)).use { stream ->
                    photo = BitmapFactory.decodeStream(stream)
                }

                val outputUri = writeBitmapToFile(
                    applicationContext,
                    photo!!,
                    PHOTO_TYPE_TEMP,
                    photoIndex
                )
                Log.d(TAG, "Saved photo to $outputUri")

                val outputData = workDataOf(KEY_PHOTO_URI to outputUri.toString())

                Result.success(outputData)
            } catch (throwable: Throwable) {
                Log.e(TAG, "Error saving photo", throwable)
                Result.failure()
            }
        }
    }
}