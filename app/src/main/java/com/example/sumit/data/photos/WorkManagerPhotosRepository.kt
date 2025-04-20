package com.example.sumit.data.photos

import Catalano.Imaging.FastBitmap
import Catalano.Imaging.Filters.Dilatation
import Catalano.Imaging.Filters.Erosion
import Catalano.Imaging.IApplyInPlace
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.net.toUri
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OverwritingInputMerger
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.sumit.utils.KEY_PAGE_COUNT
import com.example.sumit.utils.KEY_PHOTO_INDEX
import com.example.sumit.utils.KEY_PHOTO_URI
import com.example.sumit.utils.OUTPUT_PATH
import com.example.sumit.utils.PHOTO_TYPE_SEGMENTED
import com.example.sumit.utils.PHOTO_TYPE_TEMP
import com.example.sumit.utils.PROCESSING_WORK_NAME
import com.example.sumit.utils.SAVE_PHOTOS_WORK_NAME
import com.example.sumit.utils.TAG_SAVE_PHOTO_OUTPUT
import com.example.sumit.workers.CleanupWorker
import com.example.sumit.workers.ModelDownloadWorker
import com.example.sumit.workers.SavePhotoToTempWorker
import com.example.sumit.workers.SegmentPhotoWorker
import com.example.sumit.workers.TextRecognitionWorker
import com.example.sumit.workers.TextRefiningWorker
import com.example.sumit.workers.writeBitmapToFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

private const val TAG = "PhotosRepository"

class WorkManagerPhotosRepository(private val context: Context) : PhotosRepository {
    private val workManager = WorkManager.getInstance(context)

    override val movePhotosWorkData: Flow<List<WorkInfo>> =
        workManager.getWorkInfosByTagFlow(TAG_SAVE_PHOTO_OUTPUT).filter { it.isNotEmpty() }

    override fun movePhotosToTemp(photoUris: List<Uri>) {
        var continuation = workManager.beginUniqueWork(
            SAVE_PHOTOS_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            OneTimeWorkRequest.Companion.from(CleanupWorker::class.java)
        )

        val savePhotoWorkers = photoUris.mapIndexed { index, photo ->
            val builder = OneTimeWorkRequestBuilder<SavePhotoToTempWorker>()
            builder
                .addTag(TAG_SAVE_PHOTO_OUTPUT)
                .setInputData(createInputDataForWorkRequest(index, photo))
                .build()
        }
        continuation = continuation.then(savePhotoWorkers)

        continuation.enqueue()
    }

    override suspend fun getTempPhotos(): List<Uri> = getPhotoUrisByType(PHOTO_TYPE_TEMP)

    override fun cancelWork() {
        workManager.cancelUniqueWork(SAVE_PHOTOS_WORK_NAME)
    }

    override fun startSegmentation(index: Int, photoUri: Uri): UUID {
        val builder = OneTimeWorkRequestBuilder<SegmentPhotoWorker>()
        val segmentPhotoWorker = builder
            .setInputData(createInputDataForWorkRequest(index, photoUri))
            .build()
        workManager.enqueue(segmentPhotoWorker)
        return segmentPhotoWorker.id
    }

    override fun getSegmentationWorkData(id: UUID): Flow<WorkInfo> =
        workManager.getWorkInfoByIdFlow(id).filterNotNull()

    override suspend fun adjustBitmap(photoUri: Uri, amount: Int): Bitmap? {
        if (amount == 2) {
            return null
        }
        return withContext(Dispatchers.IO) {
            val resolver = context.contentResolver

            val photo = BitmapFactory.decodeStream(
                resolver.openInputStream(photoUri),
                null,
                BitmapFactory.Options().apply { inMutable = true }
            )
            val fb = FastBitmap(photo)

            ensureActive()

            val filter: IApplyInPlace = if (amount > 2) {
                Erosion((amount - 2) * 2)
            } else {
                Dilatation((2 - amount) * 2)
            }
            filter.applyInPlace(fb)

            ensureActive()

            return@withContext fb.toBitmap()
        }
    }

    override suspend fun overrideSegmentedPhoto(index: Int, photo: Bitmap): Uri =
        withContext(Dispatchers.IO) {
            writeBitmapToFile(context, photo, PHOTO_TYPE_SEGMENTED, index)
        }

    override suspend fun startProcessing() {
        val segmentedPhotoUris = getPhotoUrisByType(PHOTO_TYPE_SEGMENTED)

        var continuation = workManager.beginUniqueWork(
            PROCESSING_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            OneTimeWorkRequest.from(ModelDownloadWorker::class.java)
        )

        val recognitionWorkers = segmentedPhotoUris.mapIndexed { index, photo ->
            val builder = OneTimeWorkRequestBuilder<TextRecognitionWorker>()
            builder
                .setInputData(createInputDataForWorkRequest(index, photo))
                .build()
        }
        continuation = continuation.then(recognitionWorkers)

        val refiningWorker = OneTimeWorkRequestBuilder<TextRefiningWorker>()
            .setInputMerger(OverwritingInputMerger::class.java)
            .setInputData(workDataOf(KEY_PAGE_COUNT to segmentedPhotoUris.size))
            .build()

        continuation = continuation.then(refiningWorker)

        continuation.enqueue()
    }

    private fun createInputDataForWorkRequest(index: Int, photoUri: Uri): Data {
        val builder = Data.Builder()
        builder.putInt(KEY_PHOTO_INDEX, index)
        builder.putString(KEY_PHOTO_URI, photoUri.toString())
        return builder.build()
    }

    private suspend fun getPhotoUrisByType(type: String): List<Uri> = withContext(Dispatchers.IO) {
        val outputDirectory = File(context.filesDir, OUTPUT_PATH)
        if (outputDirectory.exists()) {
            val entries = outputDirectory.listFiles()
            if (entries != null) {
                return@withContext entries
                    .filter {
                        val name = it.name
                        name.isNotEmpty() && name.startsWith(type) && name.endsWith(".png")
                    }
                    .sortedBy { it.name }
                    .map { it.toUri() }
            }
        }
        return@withContext emptyList()
    }
}
