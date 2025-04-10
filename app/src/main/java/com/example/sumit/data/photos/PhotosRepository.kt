package com.example.sumit.data.photos

import android.net.Uri
import androidx.work.WorkInfo
import kotlinx.coroutines.flow.Flow

interface PhotosRepository {
    val movePhotosWorkData: Flow<List<WorkInfo>>
    val segmentPhotosWorkData: Flow<List<WorkInfo>>
    fun movePhotosToTemp(photoUris: List<Uri>)
    fun cancelWork()
    suspend fun getTempPhotos(): List<Uri>
    fun startSegmentation(photoUris: List<Uri>)
}
