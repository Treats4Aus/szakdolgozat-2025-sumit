package com.example.sumit.data.photos

import android.graphics.Bitmap
import android.net.Uri
import androidx.work.WorkInfo
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * Manages photos for the application.
 */
interface PhotosRepository {
    /**
     * The current state of the operation of moving temporary images to internal storage. Every item
     * in the list represents the worker behind moving a single image.
     */
    val movePhotosWorkData: Flow<List<WorkInfo>>

    /**
     * Moves the referenced images to internal storage.
     * @param photoUris The uris that point to the temporary images
     */
    fun movePhotosToTemp(photoUris: List<Uri>)

    /**
     * Cancel the moving of images to internal storage.
     */
    fun cancelWork()


    /**
     * Gets every image stored in internal storage.
     * @return List of uris that point to the images
     */
    suspend fun getTempPhotos(): List<Uri>

    /**
     * Starts segmentation on the image referenced by a uri.
     * @param index The position of the processed image in the list
     * @param photoUri The uri pointing to the image to process
     * @return The UUID of the worker handling the segmentation
     */
    fun startSegmentation(index: Int, photoUri: Uri): UUID

    /**
     * Gets segmentation work data for a specific worker.
     * @param id The UUID of the requested worker
     * @return Flow of work data for the worker
     */
    fun getSegmentationWorkData(id: UUID): Flow<WorkInfo>

    /**
     * Alters the provided image to improve text readability.
     * @param photoUri The uri pointing to the image to process
     * @param amount The weight of the modification
     * @return The resulting bitmap, or `null` if the method made no changes to the image
     */
    suspend fun adjustBitmap(photoUri: Uri, amount: Int): Bitmap?

    /**
     * Overrides an image in internal storage.
     * @param index The position of the new image in the list
     * @param photo The new image to store
     * @return The uri pointing to the overridden image
     */
    suspend fun overrideSegmentedPhoto(index: Int, photo: Bitmap): Uri


    /**
     * The work data of every stage in processing the images.
     */
    val processingWorkData: Flow<List<WorkInfo>>

    /**
     * Begins the processing of the segmented images.
     */
    suspend fun startProcessing()
}
