package com.example.sumit.data.photos

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.sumit.utils.KEY_PHOTO_INDEX
import com.example.sumit.utils.KEY_PHOTO_URI
import com.example.sumit.utils.OUTPUT_PATH
import com.example.sumit.utils.SAVE_PHOTOS_WORK_NAME
import com.example.sumit.workers.CleanupWorker
import com.example.sumit.workers.SavePhotoToTempWorker
import java.io.File

class WorkManagerPhotosRepository(private val context: Context) : PhotosRepository {
    private val workManager = WorkManager.getInstance(context)

    override fun movePhotosToTemp(photoUris: List<Uri>) {
        var continuation = workManager.beginUniqueWork(
            SAVE_PHOTOS_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            OneTimeWorkRequest.Companion.from(CleanupWorker::class.java)
        )

        val savePhotoWorkers = photoUris.mapIndexed { index, photo ->
            val builder = OneTimeWorkRequestBuilder<SavePhotoToTempWorker>()
            builder.setInputData(createInputDataForWorkRequest(index, photo)).build()
        }
        continuation = continuation.then(savePhotoWorkers)

        continuation.enqueue()
    }

    override suspend fun getTempPhotos(): List<Uri> {
        val outputDirectory = File(context.filesDir, OUTPUT_PATH)
        if (outputDirectory.exists()) {
            val entries = outputDirectory.listFiles()
            if (entries != null) {
                return entries
                    .filter {
                        val name = it.name
                        name.isNotEmpty() && name.endsWith(".png")
                    }
                    .map { it.toUri() }
            }
        }
        return emptyList()
    }

    override fun cancelWork() {
        workManager.cancelUniqueWork(SAVE_PHOTOS_WORK_NAME)
    }

    private fun createInputDataForWorkRequest(index: Int, photoUri: Uri): Data {
        val builder = Data.Builder()
        builder.putInt(KEY_PHOTO_INDEX, index)
        builder.putString(KEY_PHOTO_URI, photoUri.toString())
        return builder.build()
    }
}
