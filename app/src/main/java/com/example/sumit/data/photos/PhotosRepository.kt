package com.example.sumit.data.photos

import android.graphics.Bitmap
import android.net.Uri
import androidx.work.WorkInfo
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface PhotosRepository {
    val movePhotosWorkData: Flow<List<WorkInfo>>
    fun movePhotosToTemp(photoUris: List<Uri>)
    fun cancelWork()
    suspend fun getTempPhotos(): List<Uri>
    fun startSegmentation(index: Int, photoUri: Uri): UUID
    fun getSegmentationWorkData(id: UUID): Flow<WorkInfo>
    suspend fun adjustBitmap(photoUri: Uri, amount: Int): Bitmap?
    fun overrideSegmentedPhoto(index: Int, photo: Bitmap): UUID
}
