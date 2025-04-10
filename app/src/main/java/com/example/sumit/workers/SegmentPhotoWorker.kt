package com.example.sumit.workers

import Catalano.Imaging.FastBitmap
import Catalano.Imaging.Filters.BradleyLocalThreshold
import Catalano.Imaging.Filters.Grayscale
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.sumit.utils.KEY_PHOTO_INDEX
import com.example.sumit.utils.KEY_PHOTO_URI
import com.example.sumit.utils.KEY_SEGMENTED_BITMAP
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "SegmentPhotoWorker"

class SegmentPhotoWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
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

                val photo = BitmapFactory.decodeStream(
                    resolver.openInputStream(Uri.parse(photoUri)),
                    null,
                    BitmapFactory.Options().apply { inMutable = true }
                )
                val fb = FastBitmap(photo)

                val grayscale = Grayscale()
                grayscale.applyInPlace(fb)

                val bradley = BradleyLocalThreshold()
                bradley.applyInPlace(fb)

                val outputData = workDataOf(
                    KEY_PHOTO_INDEX to photoIndex,
                    KEY_SEGMENTED_BITMAP to fb.toBitmap()
                )
                // TODO: bitmap to uri
                Result.success(outputData)
            } catch (throwable: Throwable) {
                Log.e(TAG, "Error segmenting photo", throwable)
                Result.failure()
            }
        }
    }
}