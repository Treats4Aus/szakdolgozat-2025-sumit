package com.example.sumit.data.photos

import android.net.Uri

interface PhotosRepository {
    fun movePhotosToTemp(photoUris: List<Uri>)
    fun cancelWork()
    suspend fun getTempPhotos(): List<Uri>
}